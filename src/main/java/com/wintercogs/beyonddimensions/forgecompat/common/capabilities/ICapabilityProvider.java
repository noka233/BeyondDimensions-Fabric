package com.wintercogs.beyonddimensions.forgecompat.common.capabilities;

import com.wintercogs.beyonddimensions.forgecompat.common.util.LazyOptional;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider
{
    @NotNull
    <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);

    @NotNull
    default <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return getCapability(cap, null);
    }
}
