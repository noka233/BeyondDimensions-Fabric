package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 Fabric Storage&lt;ItemVariant&gt; 适配为模组内部的 IItemHandler。
 */
public class ItemStorageAdapter implements IItemHandler
{
    private final Storage<ItemVariant> storage;

    public ItemStorageAdapter(Storage<ItemVariant> storage)
    {
        this.storage = storage;
    }

    private List<StorageView<ItemVariant>> views()
    {
        List<StorageView<ItemVariant>> views = new ArrayList<>();
        for (StorageView<ItemVariant> view : storage)
        {
            views.add(view);
        }
        return views;
    }

    @Override
    public int getSlots()
    {
        return Math.max(1, views().size());
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot)
    {
        List<StorageView<ItemVariant>> views = views();
        if (slot >= 0 && slot < views.size())
        {
            StorageView<ItemVariant> view = views.get(slot);
            if (!view.getResource().isBlank() && view.getAmount() > 0)
            {
                return view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
        {
            return ItemStack.EMPTY;
        }
        long inserted;
        try (Transaction tx = Transaction.openOuter())
        {
            inserted = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            if (simulate)
            {
                tx.abort();
            }
            else
            {
                tx.commit();
            }
        }
        if (inserted >= stack.getCount())
        {
            return ItemStack.EMPTY;
        }
        ItemStack rest = stack.copy();
        rest.setCount(stack.getCount() - (int) inserted);
        return rest;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount <= 0)
        {
            return ItemStack.EMPTY;
        }
        ItemVariant target = ItemVariant.blank();
        for (StorageView<ItemVariant> view : storage)
        {
            if (!view.getResource().isBlank() && view.getAmount() > 0)
            {
                target = view.getResource();
                break;
            }
        }
        if (target.isBlank())
        {
            return ItemStack.EMPTY;
        }
        long extracted;
        try (Transaction tx = Transaction.openOuter())
        {
            extracted = storage.extract(target, amount, tx);
            if (simulate)
            {
                tx.abort();
            }
            else
            {
                tx.commit();
            }
        }
        return extracted > 0 ? target.toStack((int) Math.min(extracted, Integer.MAX_VALUE)) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return true;
    }
}
