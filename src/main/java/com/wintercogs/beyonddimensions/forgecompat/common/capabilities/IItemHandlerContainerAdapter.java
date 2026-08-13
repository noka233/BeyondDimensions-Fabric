package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 将模组内部的 IItemHandler 适配为原版 WorldlyContainer，供漏斗等原版机器交互。
 */
public class IItemHandlerContainerAdapter implements WorldlyContainer
{
    private final IItemHandler handler;

    public IItemHandlerContainerAdapter(IItemHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public int[] getSlotsForFace(Direction side)
    {
        int[] slots = new int[handler.getSlots()];
        for (int i = 0; i < slots.length; i++)
        {
            slots[i] = i;
        }
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction)
    {
        return handler.isItemValid(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction)
    {
        return true;
    }

    @Override
    public int getContainerSize()
    {
        return handler.getSlots();
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < handler.getSlots(); i++)
        {
            if (!handler.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return handler.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        return handler.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return handler.extractItem(index, 64, false);
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        if (handler instanceof com.wintercogs.beyonddimensions.forgecompat.items.IItemHandlerModifiable modifiable)
        {
            modifiable.setStackInSlot(index, stack);
        }
    }

    @Override
    public void setChanged()
    {
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
    }
}
