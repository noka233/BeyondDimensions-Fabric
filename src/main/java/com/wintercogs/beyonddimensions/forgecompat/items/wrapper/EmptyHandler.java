package com.wintercogs.beyonddimensions.forgecompat.items.wrapper;

import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;
import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandlerModifiable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EmptyHandler implements IItemHandlerModifiable
{
    public static final IItemHandler INSTANCE = new EmptyHandler();

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack)
    {
    }

    @Override
    public int getSlots()
    {
        return 0;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 0;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return false;
    }
}
