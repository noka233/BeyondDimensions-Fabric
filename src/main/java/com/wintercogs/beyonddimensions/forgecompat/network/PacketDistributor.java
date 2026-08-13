package com.wintercogs.beyonddimensions.forgecompat.network;

import com.wintercogs.beyonddimensions.forgecompat.network.simple.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class PacketDistributor<T>
{
    public static final PacketDistributor<Supplier<ServerPlayer>> PLAYER = new PacketDistributor<>();

    public PacketTarget with(T value)
    {
        return new PacketTarget(value);
    }

    public static class PacketTarget
    {
        private final Object value;

        public PacketTarget(Object value)
        {
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        public void send(SimpleChannel channel, Object message)
        {
            ServerPlayer player = ((Supplier<ServerPlayer>) value).get();
            channel.sendToPlayer(player, message);
        }
    }
}
