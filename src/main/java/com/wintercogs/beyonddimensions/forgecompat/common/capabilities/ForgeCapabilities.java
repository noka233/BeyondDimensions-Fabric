package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.energy.IEnergyStorage;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandler;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandlerItem;
import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;

public class ForgeCapabilities
{
    public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>();
    public static final Capability<IFluidHandler> FLUID_HANDLER = new Capability<>();
    public static final Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM = new Capability<>();
    public static final Capability<IEnergyStorage> ENERGY = new Capability<>();
}
