package com.wintercogs.beyonddimensions.forgecompat.network;

import com.wintercogs.beyonddimensions.forgecompat.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class NetworkRegistry
{
    public static SimpleChannel newSimpleChannel(ResourceLocation name, Supplier<String> protocolVersion, Predicate<String> clientAccepted, Predicate<String> serverAccepted)
    {
        return new SimpleChannel(name);
    }
}
