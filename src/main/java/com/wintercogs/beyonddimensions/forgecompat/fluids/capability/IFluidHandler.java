package com.wintercogs.beyonddimensions.forgecompat.fluids.capability;

import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandler
{
    int getTanks();

    @NotNull
    FluidStack getFluidInTank(int tank);

    int getTankCapacity(int tank);

    boolean isFluidValid(int tank, @NotNull FluidStack stack);

    int fill(FluidStack resource, FluidAction action);

    @NotNull
    FluidStack drain(FluidStack resource, FluidAction action);

    @NotNull
    FluidStack drain(int maxDrain, FluidAction action);

    enum FluidAction
    {
        SIMULATE,
        EXECUTE;

        public boolean simulate()
        {
            return this == SIMULATE;
        }
    }
}
