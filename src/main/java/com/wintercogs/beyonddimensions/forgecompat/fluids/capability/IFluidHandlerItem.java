package com.wintercogs.beyonddimensions.forgecompat.fluids.capability;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IFluidHandlerItem extends IFluidHandler
{
    @NotNull
    ItemStack getContainer();
}
