package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.common.util.LazyOptional;
import com.wintercogs.beyonddimensions.forgecompat.fluids.capability.IFluidHandler;
import com.wintercogs.beyonddimensions.forgecompat.items.IItemHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;

public class CapabilityCompat
{
    public static <T> LazyOptional<T> getCapability(Object provider, Capability<T> cap, Direction side)
    {
        if (provider instanceof ICapabilityProvider p)
        {
            LazyOptional<T> direct = p.getCapability(cap, side);
            if (direct.isPresent())
            {
                return direct;
            }
        }
        if (provider instanceof BlockEntity be && be.getLevel() != null)
        {
            if (cap == ForgeCapabilities.ITEM_HANDLER)
            {
                Storage<ItemVariant> storage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), side);
                if (storage != null)
                {
                    return LazyOptional.of(() -> (T) new ItemStorageAdapter(storage));
                }
            }
            if (cap == ForgeCapabilities.FLUID_HANDLER)
            {
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(be.getLevel(), be.getBlockPos(), side);
                if (storage != null)
                {
                    return LazyOptional.of(() -> (T) new FluidStorageAdapter(storage, null));
                }
                FluidState fluidState = be.getLevel().getFluidState(be.getBlockPos());
                if (!fluidState.isEmpty())
                {
                    return LazyOptional.of(() -> (T) new WorldFluidHandler(be.getLevel(), be.getBlockPos(), fluidState));
                }
            }
        }
        if (provider instanceof ItemStack stack)
        {
            if (cap == ForgeCapabilities.FLUID_HANDLER_ITEM)
            {
                ContainerItemContext ctx = ContainerItemContext.withInitial(stack.copy());
                Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ctx);
                if (storage != null)
                {
                    return LazyOptional.of(() -> (T) new FluidStorageAdapter(storage, ctx));
                }
            }
        }
        return LazyOptional.empty();
    }

    public static <T> LazyOptional<T> getCapability(Object provider, Capability<T> cap)
    {
        return getCapability(provider, cap, null);
    }

    /**
     * 在菜单交互（点击槽位）中获取手持/光标物品的流体能力。
     * 使用 {@link ContainerItemContext#ofPlayerCursor} 让上下文绑定到菜单光标槽，
     * 这样桶/储罐在 drain/fill 后 getContainer() 能拿到更新后的携带物。
     */
    public static <T> LazyOptional<T> getCapability(Object provider, Capability<T> cap, Player player, AbstractContainerMenu menu)
    {
        if (provider instanceof ItemStack stack && cap == ForgeCapabilities.FLUID_HANDLER_ITEM)
        {
            ContainerItemContext ctx = ContainerItemContext.ofPlayerCursor(player, menu);
            Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ctx);
            if (storage != null)
            {
                return LazyOptional.of(() -> (T) new FluidStorageAdapter(storage, ctx));
            }
        }
        return getCapability(provider, cap, (Direction) null);
    }
}
