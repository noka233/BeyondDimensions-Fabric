package com.wintercogs.beyonddimensions.api.dimensionnet;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import com.wintercogs.beyonddimensions.forgecompat.event.server.ServerStartedEvent;
import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.SubscribeEvent;
import com.wintercogs.beyonddimensions.forgecompat.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = BDConstants.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PlayerNetIndex extends BackedUpSavedData
{
    static final String DATA_NAME = "BDPlayerNetIndex";
    static final int NO_PRIMARY_NET = -1;

    private static final String PRIMARY_NET_ENTRIES = "PrimaryNetEntries";
    private static final String PLAYER_ID = "PlayerId";
    private static final String PRIMARY_NET_ID = "PrimaryNetId";

    private final Map<UUID, Integer> primaryNetIds = new HashMap<>();
    private final Map<UUID, LinkedHashSet<Integer>> allNetIds = new HashMap<>();

    static PlayerNetIndex get(MinecraftServer server)
    {
        return NetSaveBackupHelper.safeLoad(server, DATA_NAME, PlayerNetIndex::load, PlayerNetIndex::new, true);
    }

    static PlayerNetIndex getIfPresent(MinecraftServer server)
    {
        return NetSaveBackupHelper.safeLoad(server, DATA_NAME, PlayerNetIndex::load, PlayerNetIndex::new, false);
    }

    static PlayerNetIndex load(CompoundTag tag)
    {
        try
        {
            return doLoad(tag);
        }
        catch (Exception e)
        {
            LOGGER.error("[BD-SAFE] 玩家网络索引存档解析失败，已保留原始文件并尝试从备份恢复", e);
            PlayerNetIndex index = new PlayerNetIndex();
            index.markLoadFailed();
            return index;
        }
    }

    private static PlayerNetIndex doLoad(CompoundTag tag)
    {
        PlayerNetIndex index = new PlayerNetIndex();
        ListTag entryList = tag.getList(PRIMARY_NET_ENTRIES, CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < entryList.size(); i++)
        {
            CompoundTag entry = entryList.getCompound(i);
            if (!entry.hasUUID(PLAYER_ID) || !entry.contains(PRIMARY_NET_ID))
            {
                continue;
            }
            index.primaryNetIds.put(entry.getUUID(PLAYER_ID), entry.getInt(PRIMARY_NET_ID));
        }
        return index;
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        ListTag entryList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : copyPrimaryNetIds().entrySet())
        {
            CompoundTag data = new CompoundTag();
            data.putUUID(PLAYER_ID, entry.getKey());
            data.putInt(PRIMARY_NET_ID, entry.getValue());
            entryList.add(data);
        }
        tag.put(PRIMARY_NET_ENTRIES, entryList);
        return tag;
    }

    void clearRuntime()
    {
        allNetIds.clear();
    }

    void rebuildFromServer(MinecraftServer server)
    {
        clearRuntime();
        for (int netId : NetRegistryIndex.get(server).getActiveNetIds(server))
        {
            DimensionsNet net = DimensionsNet.getNetFromId(server, netId);
            if (net == null)
            {
                continue;
            }
            for (UUID playerId : net.getPlayers())
            {
                addMembership(playerId, net.getId(), false);
            }
        }
        if (reconcilePrimaryMappings())
        {
            setDirty();
        }
    }

    void addMembership(UUID playerId, int netId, boolean switchPrimary)
    {
        if (netId < 0)
        {
            return;
        }

        LinkedHashSet<Integer> memberships = allNetIds.computeIfAbsent(playerId, ignored -> new LinkedHashSet<>());
        if (memberships.add(netId))
        {
            if (switchPrimary)
            {
                primaryNetIds.put(playerId, netId);
            }
            setDirty();
        }
    }

    void removeMembership(UUID playerId, int netId)
    {
        LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
        if (memberships == null || !memberships.remove(netId))
        {
            return;
        }

        if (memberships.isEmpty())
        {
            allNetIds.remove(playerId);
            primaryNetIds.remove(playerId);
            setDirty();
            return;
        }

        Integer primaryNetId = primaryNetIds.get(playerId);
        if (primaryNetId != null && primaryNetId == netId)
        {
            primaryNetIds.put(playerId, getSmallestNetId(memberships));
        }
        setDirty();
    }

    void clearPrimary(UUID playerId)
    {
        LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
        if (memberships == null || memberships.isEmpty())
        {
            if (primaryNetIds.remove(playerId) != null)
            {
                setDirty();
            }
            return;
        }

        Integer previous = primaryNetIds.put(playerId, NO_PRIMARY_NET);
        if (previous == null || previous != NO_PRIMARY_NET)
        {
            setDirty();
        }
    }

    boolean setPrimary(UUID playerId, int netId)
    {
        boolean changed;
        if (netId == NO_PRIMARY_NET)
        {
            LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
            if (memberships == null || memberships.isEmpty())
            {
                changed = primaryNetIds.remove(playerId) != null;
            }
            else
            {
                Integer previous = primaryNetIds.put(playerId, NO_PRIMARY_NET);
                changed = previous == null || previous != NO_PRIMARY_NET;
            }
        }
        else
        {
            LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
            if (memberships == null || !memberships.contains(netId))
            {
                return false;
            }

            Integer previous = primaryNetIds.put(playerId, netId);
            changed = previous == null || previous != netId;
        }

        if (changed)
        {
            setDirty();
        }
        return changed;
    }

    int getPrimaryNetId(UUID playerId)
    {
        return primaryNetIds.getOrDefault(playerId, NO_PRIMARY_NET);
    }

    boolean hasAnyMembership(UUID playerId)
    {
        LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
        return memberships != null && !memberships.isEmpty();
    }

    List<Integer> getAllNetIds(UUID playerId)
    {
        LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
        return memberships == null || memberships.isEmpty() ? List.of() : new ArrayList<>(memberships);
    }

    Map<UUID, Integer> copyPrimaryNetIds()
    {
        return new HashMap<>(primaryNetIds);
    }

    boolean reconcilePrimaryMappings()
    {
        boolean changed = false;
        Set<UUID> playerIds = new HashSet<>(allNetIds.keySet());
        playerIds.addAll(primaryNetIds.keySet());

        for (UUID playerId : playerIds)
        {
            LinkedHashSet<Integer> memberships = allNetIds.get(playerId);
            if (memberships == null || memberships.isEmpty())
            {
                changed |= primaryNetIds.remove(playerId) != null;
                continue;
            }

            if (!primaryNetIds.containsKey(playerId))
            {
                primaryNetIds.put(playerId, getSmallestNetId(memberships));
                changed = true;
                continue;
            }

            int primaryNetId = primaryNetIds.get(playerId);
            if (primaryNetId == NO_PRIMARY_NET)
            {
                continue;
            }

            if (!memberships.contains(primaryNetId))
            {
                primaryNetIds.put(playerId, getSmallestNetId(memberships));
                changed = true;
            }
        }
        return changed;
    }

    private static int getSmallestNetId(LinkedHashSet<Integer> memberships)
    {
        int smallest = Integer.MAX_VALUE;
        for (int membership : memberships)
        {
            smallest = Math.min(smallest, membership);
        }
        return smallest;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event)
    {
        PlayerNetIndex.get(event.getServer()).rebuildFromServer(event.getServer());
    }
}
