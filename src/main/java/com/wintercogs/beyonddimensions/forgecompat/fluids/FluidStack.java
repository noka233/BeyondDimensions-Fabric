package com.wintercogs.beyonddimensions.forgecompat.fluids;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidStack
{
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private final Fluid fluid;
    private int amount;
    @Nullable
    private CompoundTag tag;

    public FluidStack(Fluid fluid, int amount)
    {
        this(fluid, amount, null);
    }

    public FluidStack(Fluid fluid, int amount, @Nullable CompoundTag tag)
    {
        this.fluid = fluid;
        this.amount = amount;
        this.tag = tag;
    }

    public FluidStack(FluidStack stack, int amount)
    {
        this(stack.fluid, amount, stack.tag != null ? stack.tag.copy() : null);
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public boolean isEmpty()
    {
        return fluid == Fluids.EMPTY || amount <= 0;
    }

    public boolean hasTag()
    {
        return tag != null;
    }

    public Component getDisplayName()
    {
        if (isEmpty())
        {
            return Component.empty();
        }
        ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
        return Component.translatable("fluid." + key.getNamespace() + "." + key.getPath());
    }

    public FluidStack copy()
    {
        return new FluidStack(fluid, amount, tag != null ? tag.copy() : null);
    }

    @Nullable
    public CompoundTag getTag()
    {
        return tag;
    }

    public void setTag(@Nullable CompoundTag tag)
    {
        this.tag = tag;
    }

    public boolean isFluidEqual(FluidStack other)
    {
        return other != null && fluid == other.fluid && Objects.equals(tag, other.tag);
    }

    public static FluidStack loadFluidStackFromNBT(CompoundTag nbt)
    {
        Fluid fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.get(new net.minecraft.resources.ResourceLocation(nbt.getString("FluidName")));
        if (fluid == null)
        {
            return EMPTY;
        }
        int amount = nbt.getInt("Amount");
        CompoundTag tag = nbt.contains("Tag") ? nbt.getCompound("Tag") : null;
        return new FluidStack(fluid, amount, tag);
    }

    public CompoundTag writeToNBT(CompoundTag nbt)
    {
        nbt.putString("FluidName", net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluid).toString());
        nbt.putInt("Amount", amount);
        if (tag != null)
        {
            nbt.put("Tag", tag);
        }
        return nbt;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof FluidStack that))
        {
            return false;
        }
        return amount == that.amount && fluid == that.fluid && Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(fluid, amount, tag);
    }
}
