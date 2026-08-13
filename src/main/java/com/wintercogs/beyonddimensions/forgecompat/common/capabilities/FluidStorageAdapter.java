package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidStack;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandler;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandlerItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 将 Fabric Storage&lt;FluidVariant&gt; 适配为模组内部的 IFluidHandler。
 */
public class FluidStorageAdapter implements IFluidHandlerItem
{
    /** Fabric API 以“滴”为单位：1 桶 = 81000 滴。 */
    private static final long DROPLETS_PER_BUCKET = FluidConstants.BUCKET;
    /** 模组内部（Forge 兼容层）以 mb 为单位：1 桶 = 1000 mb。 */
    private static final long MB_PER_BUCKET = 1000L;

    private final Storage<FluidVariant> storage;
    private final ContainerItemContext context;

    public FluidStorageAdapter(Storage<FluidVariant> storage, ContainerItemContext context)
    {
        this.storage = storage;
        this.context = context;
    }

    private static long dropletsToMb(long droplets)
    {
        return droplets * MB_PER_BUCKET / DROPLETS_PER_BUCKET;
    }

    private static long mbToDroplets(long mb)
    {
        return mb * DROPLETS_PER_BUCKET / MB_PER_BUCKET;
    }

    @Override
    public @NotNull ItemStack getContainer()
    {
        if (context != null)
        {
            var mainSlot = context.getMainSlot();
            var variant = mainSlot.getResource();
            if (variant.isBlank())
            {
                return ItemStack.EMPTY;
            }
            return variant.toStack((int) Math.min(mainSlot.getAmount(), Integer.MAX_VALUE));
        }
        return ItemStack.EMPTY;
    }

    private List<StorageView<FluidVariant>> views()
    {
        List<StorageView<FluidVariant>> views = new ArrayList<>();
        for (StorageView<FluidVariant> view : storage)
        {
            views.add(view);
        }
        return views;
    }

    @Override
    public int getTanks()
    {
        return Math.max(1, views().size());
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank)
    {
        List<StorageView<FluidVariant>> views = views();
        if (tank >= 0 && tank < views.size())
        {
            StorageView<FluidVariant> view = views.get(tank);
            if (!view.getResource().isBlank() && view.getAmount() > 0)
            {
                FluidVariant v = view.getResource();
                long amountMb = dropletsToMb(view.getAmount());
                return new FluidStack(v.getFluid(), (int) Math.min(amountMb, Integer.MAX_VALUE), v.getNbt());
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        List<StorageView<FluidVariant>> views = views();
        if (tank >= 0 && tank < views.size())
        {
            long capacityDroplets = views.get(tank).getCapacity();
            if (capacityDroplets >= 0)
            {
                return (int) Math.min(dropletsToMb(capacityDroplets), Integer.MAX_VALUE);
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource == null || resource.isEmpty())
        {
            return 0;
        }
        long inserted;
        try (Transaction tx = Transaction.openOuter())
        {
            long amountDroplets = mbToDroplets(resource.getAmount());
            inserted = storage.insert(FluidVariant.of(resource.getFluid(), resource.getTag()), amountDroplets, tx);
            if (action.simulate())
            {
                tx.abort();
            }
            else
            {
                tx.commit();
            }
        }
        return (int) dropletsToMb(inserted);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource == null || resource.isEmpty())
        {
            return FluidStack.EMPTY;
        }
        FluidVariant target = FluidVariant.of(resource.getFluid(), resource.getTag());
        long extracted;
        try (Transaction tx = Transaction.openOuter())
        {
            long amountDroplets = mbToDroplets(resource.getAmount());
            extracted = storage.extract(target, amountDroplets, tx);
            if (action.simulate())
            {
                tx.abort();
            }
            else
            {
                tx.commit();
            }
        }
        if (extracted <= 0)
        {
            return FluidStack.EMPTY;
        }
        return new FluidStack(resource.getFluid(), (int) dropletsToMb(extracted), resource.getTag());
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action)
    {
        if (maxDrain <= 0)
        {
            return FluidStack.EMPTY;
        }
        FluidVariant target = FluidVariant.blank();
        for (StorageView<FluidVariant> view : storage)
        {
            if (!view.getResource().isBlank() && view.getAmount() > 0)
            {
                target = view.getResource();
                break;
            }
        }
        if (target.isBlank())
        {
            return FluidStack.EMPTY;
        }
        long extracted;
        try (Transaction tx = Transaction.openOuter())
        {
            long amountDroplets = mbToDroplets(maxDrain);
            extracted = storage.extract(target, amountDroplets, tx);
            if (action.simulate())
            {
                tx.abort();
            }
            else
            {
                tx.commit();
            }
        }
        if (extracted <= 0)
        {
            return FluidStack.EMPTY;
        }
        return new FluidStack(target.getFluid(), (int) dropletsToMb(extracted), target.getNbt());
    }
}
