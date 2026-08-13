package com.wintercogs.beyonddimensions.util;

import net.minecraft.core.Direction;
import com.wintercogs.beyonddimensions.forgecompat.common.capabilities.Capability;

import org.jetbrains.annotations.Nullable;

public record SidedCapId(Capability<?> cap, @Nullable Direction sided)
{
}
