package com.wintercogs.beyonddimensions.common.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * 第三方联动通道方块：复用核心通道逻辑，掉落自身（无需战利品表文件）
 */
public class CompatPathwayBlock extends NetPathwayBlock
{
    public CompatPathwayBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return List.of(new ItemStack(this));
    }
}
