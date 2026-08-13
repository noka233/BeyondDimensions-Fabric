package com.wintercogs.beyonddimensions.integration;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.api.ids.BDBlockIds;
import com.wintercogs.beyonddimensions.api.ids.BDItemIds;
import com.wintercogs.beyonddimensions.common.block.CompatPathwayBlock;
import com.wintercogs.beyonddimensions.common.block.entity.NetPathwayBlockEntity;
import com.wintercogs.beyonddimensions.common.init.BDBlocks;
import com.wintercogs.beyonddimensions.common.item.NetedItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;

/**
 * 第三方联动内容的条件注册。
 * 与原模组一致：检测到对应模组才注册物品/方块；未检测到则隐藏。
 * 说明：方块复用核心通道逻辑，第三方存储/能量的深度桥接暂未移植。
 */
public class CompatRegistry
{
    public static final List<Item> BLOCK_COMPAT_ITEMS = new ArrayList<>();
    public static final List<Item> ITEM_COMPAT_ITEMS = new ArrayList<>();

    public static void register()
    {
        if (ModPresence.isLoaded(OtherModIds.REFINED_STORAGE))
        {
            registerPathway(BDBlockIds.RS_NET_PATHWAY);
        }
        if (ModPresence.isLoaded(OtherModIds.BOTANIA))
        {
            registerPathway(BDBlockIds.MANA_POOL_PATHWAY);
        }
        if (ModPresence.isLoaded(OtherModIds.ARS_NOUVEAU))
        {
            registerPathway(BDBlockIds.ARS_SOURCE_PATHWAY);
        }
        if (ModPresence.isLoaded(OtherModIds.CREATE))
        {
            registerPathway(BDBlockIds.SCHEMATICANNON_PATHWAY);
        }
        if (ModPresence.isLoaded(OtherModIds.AE2))
        {
            Item item = new NetedItem(new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(BDConstants.MODID, BDItemIds.NET_AE_STORAGE_CELL), item);
            ITEM_COMPAT_ITEMS.add(item);
        }
    }

    private static void registerPathway(String name)
    {
        Block block = new CompatPathwayBlock(BlockBehaviour.Properties.copy(BDBlocks.NET_PATHWAY.get()));
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(BDConstants.MODID, name), block);
        Item item = new BlockItem(block, new Item.Properties());
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(BDConstants.MODID, name), item);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(BDConstants.MODID, name + "_block_entity"),
                BlockEntityType.Builder.of(NetPathwayBlockEntity::new, block).build(null));
        BLOCK_COMPAT_ITEMS.add(item);
    }
}
