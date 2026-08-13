package com.wintercogs.beyonddimensions.common.fluid;

import com.wintercogs.beyonddimensions.api.ids.BDConstants;
import com.wintercogs.beyonddimensions.common.init.BDFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

public abstract class XpFluid extends FlowingFluid
{
    @Override
    public Item getBucket()
    {
        return BDFluids.XP_FLUID.bucket().get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction)
    {
        return true;
    }

    @Override
    public boolean isSame(Fluid fluid)
    {
        return fluid instanceof XpFluid;
    }

    @Override
    public Fluid getFlowing()
    {
        return BDFluids.XP_FLUID.flowing().get();
    }

    @Override
    public Fluid getSource()
    {
        return BDFluids.XP_FLUID.source().get();
    }

    @Override
    protected boolean canConvertToSource(Level level)
    {
        return false;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
    }

    @Override
    protected int getSlopeFindDistance(LevelReader level)
    {
        return 4;
    }

    @Override
    protected int getDropOff(LevelReader level)
    {
        // 与 Forge 原版 ForgeFlowingFluid.Properties.levelDecreasePerBlock(2) 一致
        return 2;
    }

    @Override
    public int getTickDelay(LevelReader level)
    {
        return 5;
    }

    @Override
    protected float getExplosionResistance()
    {
        return 100F;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state)
    {
        // LiquidBlock.LEVEL=0 represents a source. Preserve the flowing fluid's
        // legacy level here, otherwise every propagated block becomes a source.
        return BDFluids.XP_FLUID.block().get().defaultBlockState()
                .setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    public static class Source extends XpFluid
    {
        @Override
        public boolean isSource(FluidState state)
        {
            return true;
        }

        @Override
        public int getAmount(FluidState state)
        {
            return 8;
        }
    }

    public static class Flowing extends XpFluid
    {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder)
        {
            super.createFluidStateDefinition(builder);
            builder.add(FlowingFluid.LEVEL);
        }

        @Override
        public boolean isSource(FluidState state)
        {
            return false;
        }

        @Override
        public int getAmount(FluidState state)
        {
            return state.getValue(FlowingFluid.LEVEL);
        }
    }
}
