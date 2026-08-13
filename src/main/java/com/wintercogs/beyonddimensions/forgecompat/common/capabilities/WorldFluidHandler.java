package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidStack;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

/**
 * 世界流体方块（水/岩浆等）的只读 IFluidHandler，供网络泵等抽取。
 */
public class WorldFluidHandler implements IFluidHandler
{
    private final Level level;
    private final BlockPos pos;
    private final Fluid fluid;

    public WorldFluidHandler(Level level, BlockPos pos, FluidState state)
    {
        this.level = level;
        this.pos = pos;
        this.fluid = state.getType();
    }

    @Override
    public int getTanks()
    {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        FluidState state = level.getFluidState(pos);
        if (state.isEmpty())
        {
            return FluidStack.EMPTY;
        }
        int amount = state.isSource() ? 1000 : 250;
        return new FluidStack(state.getType(), amount);
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return 1000;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
    {
        FluidState state = level.getFluidState(pos);
        if (state.isEmpty() || !state.getType().isSame(resource.getFluid()))
        {
            return FluidStack.EMPTY;
        }
        int amount = Math.min(resource.getAmount(), state.isSource() ? 1000 : 250);
        if (action == FluidAction.EXECUTE)
        {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return new FluidStack(resource.getFluid(), amount, resource.getTag());
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action)
    {
        FluidState state = level.getFluidState(pos);
        if (state.isEmpty())
        {
            return FluidStack.EMPTY;
        }
        int amount = Math.min(maxDrain, state.isSource() ? 1000 : 250);
        if (action == FluidAction.EXECUTE)
        {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
        return new FluidStack(state.getType(), amount);
    }
}
