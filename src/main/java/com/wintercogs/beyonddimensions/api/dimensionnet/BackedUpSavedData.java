package com.wintercogs.beyonddimensions.api.dimensionnet;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

import java.io.File;

/**
 * 带“写盘备份 + 加载失败保护”的 SavedData 基类。
 * <p>
 * 原版 1.20.1 的 {@link SavedData#save(File)} 会直接覆盖目标文件，
 * 而 {@link net.minecraft.world.level.storage.DimensionDataStorage} 在反序列化抛异常时
 * 会静默返回 null，随后被空的新实例覆盖写盘，导致玩家网络中的物品永久丢失。
 * 本基类在每次写盘前通过 {@link NetSaveBackupHelper} 轮换 .bak 备份，并在加载失败时
 * 禁止覆盖原文件，保证原始数据至少保留在磁盘上可人工恢复。
 */
public abstract class BackedUpSavedData extends SavedData
{
    protected static final Logger LOGGER = LogUtils.getLogger();

    private boolean loadFailed = false;

    protected BackedUpSavedData()
    {
    }

    /**
     * 本次加载是否失败。失败实例的内容可能不完整，任何保存操作都必须跳过，
     * 以免覆盖仍可能包含玩家物品的原始文件。
     */
    public final boolean loadFailed()
    {
        return loadFailed;
    }

    protected final void markLoadFailed()
    {
        this.loadFailed = true;
    }

    @Override
    public void save(File file)
    {
        if (!isDirty())
        {
            return;
        }

        if (loadFailed)
        {
            LOGGER.warn("[BD-SAFE] 跳过 {} 的写入：源数据加载失败，保留原文件以防网络物品丢失", file);
            setDirty(false);
            return;
        }

        try
        {
            CompoundTag root = new CompoundTag();
            root.put("data", this.save(new CompoundTag()));
            NbtUtils.addCurrentDataVersion(root);
            if (NetSaveBackupHelper.saveWithBackup(file, root))
            {
                setDirty(false);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 序列化 {} 失败，保留脏标记等待下次保存重试", file, e);
        }
    }
}
