package com.wintercogs.beyonddimensions.mixin;

import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.Capability;
import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.ICapabilityProvider;
import com.wintercogs.beyonddimensions.forgecompat.common.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCapabilityMixin implements ICapabilityProvider
{
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return LazyOptional.empty();
    }
}
