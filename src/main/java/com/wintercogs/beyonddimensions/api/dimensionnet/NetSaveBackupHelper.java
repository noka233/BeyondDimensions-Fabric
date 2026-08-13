package com.wintercogs.beyonddimensions.api.dimensionnet;

import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 维度网络存档的“防爆炸”工具类：
 * <ul>
 *     <li>写盘前把旧文件轮换为 .bak / .bak1 / .bak2，再原子替换主文件；</li>
 *     <li>加载失败时依次尝试主文件与各备份；</li>
 *     <li>全部失败时把损坏文件复制为 .corrupt-时间戳，绝不直接丢弃；</li>
 *     <li>恢复成功后才允许新数据覆盖主文件。</li>
 * </ul>
 */
public final class NetSaveBackupHelper
{
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String[] BACKUP_SUFFIXES = {".bak", ".bak1", ".bak2"};
    private static final String CORRUPT_PREFIX = ".corrupt-";
    private static final int LEGACY_DATA_VERSION = 1343;

    /**
     * 已判定“主文件与全部备份均不可恢复”的存档路径，避免每次访问都重复读盘扫描。
     * 服务端重启后清空，用户手动修复文件后重启服务端即可重新尝试。
     */
    private static final Set<String> UNRECOVERABLE = new HashSet<>();

    private NetSaveBackupHelper()
    {
    }

    public static Path dataFolder(MinecraftServer server)
    {
        return server.getWorldPath(LevelResource.ROOT).resolve("data");
    }

    public static Path mainFile(MinecraftServer server, String dataName)
    {
        return dataFolder(server).resolve(dataName + ".dat");
    }

    /**
     * 安全加载：先走原版缓存/文件读取，失败时自动尝试 .bak 备份，
     * 全部失败则隔离损坏文件。createIfMissing 为 true 时返回可用的新实例，
     * 否则返回 null（调用方按“网络不存在”处理，绝不拿空数据覆盖原文件）。
     */
    public static <T extends BackedUpSavedData> T safeLoad(
            MinecraftServer server,
            String dataName,
            Function<CompoundTag, T> deserializer,
            Supplier<T> factory,
            boolean createIfMissing)
    {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        T cached = storage.get(deserializer, dataName);
        if (cached != null && !cached.loadFailed())
        {
            return cached;
        }

        Path main = mainFile(server, dataName);
        String mainKey = main.toAbsolutePath().toString();
        List<Path> candidates = candidateFiles(main);
        boolean anyFileExists = false;
        for (Path candidate : candidates)
        {
            if (Files.isRegularFile(candidate))
            {
                anyFileExists = true;
                break;
            }
        }

        if (anyFileExists && !UNRECOVERABLE.contains(mainKey))
        {
            for (Path candidate : candidates)
            {
                T recovered = tryDeserialize(server, candidate, deserializer);
                if (recovered == null)
                {
                    continue;
                }

                if (!main.equals(candidate) && Files.isRegularFile(main))
                {
                    quarantine(main);
                }
                if (cached == null || cached.loadFailed())
                {
                    storage.set(dataName, recovered);
                }
                if (!main.equals(candidate))
                {
                    restoreFromBackup(main, candidate);
                }
                recovered.setDirty();
                LOGGER.warn("[BD-SAFE] 从 {} 成功恢复 {}，数据已重新写回主文件",
                        candidate.getFileName(), dataName);
                return recovered;
            }

            quarantine(main);
            UNRECOVERABLE.add(mainKey);
            LOGGER.error("[BD-SAFE] {} 的主文件与所有备份均无法解析，已保留 .corrupt 副本供人工恢复",
                    dataName);
        }

        if (createIfMissing)
        {
            T fresh = factory.get();
            storage.set(dataName, fresh);
            return fresh;
        }
        return null;
    }

    /**
     * 把 NBT 根节点写入目标文件：先写临时文件，轮换旧备份，再原子替换。
     *
     * @return true 表示写盘成功，false 表示失败（调用方应保留脏标记以便重试）
     */
    public static boolean saveWithBackup(File file, CompoundTag root)
    {
        Path main = file.toPath();
        Path parent = main.getParent();
        if (parent == null)
        {
            LOGGER.error("[BD-SAFE] {} 没有父目录，无法保存", file);
            return false;
        }

        Path temp = null;
        try
        {
            temp = Files.createTempFile(parent, main.getFileName().toString() + "-", ".tmp");
            NbtIo.writeCompressed(root, temp.toFile());
            rotateBackups(main);
            moveAtomic(temp, main);
            return true;
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 保存 {} 失败，数据保留在内存中，下次保存会重试", main, e);
            try
            {
                Path bak = sibling(main, ".bak");
                if (!Files.exists(main) && Files.exists(bak))
                {
                    Files.move(bak, main, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            catch (IOException ignored)
            {
            }
            return false;
        }
        finally
        {
            if (temp != null)
            {
                try
                {
                    Files.deleteIfExists(temp);
                }
                catch (IOException ignored)
                {
                }
            }
        }
    }

    private static List<Path> candidateFiles(Path main)
    {
        List<Path> candidates = new ArrayList<>();
        candidates.add(main);
        for (String suffix : BACKUP_SUFFIXES)
        {
            candidates.add(sibling(main, suffix));
        }
        return candidates;
    }

    private static Path sibling(Path file, String suffix)
    {
        return file.resolveSibling(file.getFileName().toString() + suffix);
    }

    private static <T extends BackedUpSavedData> T tryDeserialize(
            MinecraftServer server,
            Path file,
            Function<CompoundTag, T> deserializer)
    {
        if (!Files.isRegularFile(file))
        {
            return null;
        }

        try
        {
            CompoundTag root = readRoot(file);
            if (root == null || !root.contains("data", CompoundTag.TAG_COMPOUND))
            {
                LOGGER.error("[BD-SAFE] {} 不是有效的存档容器", file);
                return null;
            }

            int fromVersion = NbtUtils.getDataVersion(root, LEGACY_DATA_VERSION);
            root = DataFixTypes.SAVED_DATA.update(
                    server.getFixerUpper(),
                    root,
                    fromVersion,
                    SharedConstants.getCurrentVersion().getDataVersion().getVersion());

            T result = deserializer.apply(root.getCompound("data"));
            if (result == null || result.loadFailed())
            {
                LOGGER.error("[BD-SAFE] {} 的内容无法反序列化", file);
                return null;
            }
            return result;
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 读取 {} 失败", file, e);
            return null;
        }
    }

    private static CompoundTag readRoot(Path file)
    {
        try
        {
            if (isGzip(file))
            {
                return NbtIo.readCompressed(file.toFile());
            }
            return NbtIo.read(file.toFile());
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 无法读取 {} 的 NBT 数据", file, e);
            return null;
        }
    }

    private static boolean isGzip(Path file) throws IOException
    {
        try (InputStream in = Files.newInputStream(file))
        {
            int first = in.read();
            if (first < 0)
            {
                return false;
            }
            int second = in.read();
            if (second < 0)
            {
                return false;
            }
            return (first & 0xFF) == 0x1F && (second & 0xFF) == 0x8B;
        }
    }

    private static void rotateBackups(Path main) throws IOException
    {
        Path bak2 = sibling(main, ".bak2");
        Path bak1 = sibling(main, ".bak1");
        Path bak = sibling(main, ".bak");

        Files.deleteIfExists(bak2);
        if (Files.exists(bak1))
        {
            Files.move(bak1, bak2, StandardCopyOption.REPLACE_EXISTING);
        }
        if (Files.exists(bak))
        {
            Files.move(bak, bak1, StandardCopyOption.REPLACE_EXISTING);
        }
        if (Files.exists(main))
        {
            Files.move(main, bak, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void moveAtomic(Path from, Path to) throws IOException
    {
        try
        {
            Files.move(from, to, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AtomicMoveNotSupportedException e)
        {
            Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void restoreFromBackup(Path main, Path backup)
    {
        try
        {
            Path temp = Files.createTempFile(main.getParent(), main.getFileName().toString() + "-", ".restore");
            try
            {
                Files.copy(backup, temp, StandardCopyOption.REPLACE_EXISTING);
                moveAtomic(temp, main);
            }
            finally
            {
                Files.deleteIfExists(temp);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 把备份 {} 写回主文件 {} 失败，稍后保存时会重试", backup, main, e);
        }
    }

    private static void quarantine(Path main)
    {
        if (!Files.isRegularFile(main))
        {
            return;
        }

        String stamp = new SimpleDateFormat("yyyyMMdd-HHmmss-SSS").format(new Date());
        Path target = sibling(main, CORRUPT_PREFIX + stamp);
        try
        {
            Files.copy(main, target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.error("[BD-SAFE] 已保留损坏文件副本：{}", target.toAbsolutePath());
        }
        catch (IOException e)
        {
            LOGGER.error("[BD-SAFE] 保留损坏文件副本失败：{}", main, e);
        }
    }
}
