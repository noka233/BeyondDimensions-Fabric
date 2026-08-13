package com.wintercogs.beyonddimensions.forgecompat.network.simple;

import com.wintercogs.beyonddimensions.forgecompat.network.NetworkDirection;
import com.wintercogs.beyonddimensions.forgecompat.network.NetworkEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel
{
    private final ResourceLocation channelId;
    private final Map<Class<?>, Registration> registrations = new LinkedHashMap<>();

    public SimpleChannel(ResourceLocation channelId)
    {
        this.channelId = channelId;
    }

    public <MSG> void registerMessage(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler)
    {
        registerMessage(id, type, encoder, decoder, handler, Optional.empty());
    }

    public <MSG> void registerMessage(int id, Class<MSG> type, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler, Optional<NetworkDirection> direction)
    {
        registrations.put(type, new Registration(id, encoder, decoder, handler, direction.orElse(null)));
    }

    @SuppressWarnings("unchecked")
    private <MSG> ResourceLocation idFor(MSG message)
    {
        return new ResourceLocation(channelId.getNamespace(), channelId.getPath() + "_" + message.getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT));
    }

    @SuppressWarnings("unchecked")
    public <MSG> void sendToServer(MSG message)
    {
        Registration reg = registrations.get(message.getClass());
        if (reg == null)
        {
            throw new IllegalArgumentException("Unregistered message " + message.getClass().getName());
        }
        FriendlyByteBuf buf = PacketByteBufs.create();
        ((BiConsumer<MSG, FriendlyByteBuf>) reg.encoder).accept(message, buf);
        ClientPlayNetworking.send(idFor(message), buf);
    }

    @SuppressWarnings("unchecked")
    public <MSG> void sendToPlayer(ServerPlayer player, MSG message)
    {
        Registration reg = registrations.get(message.getClass());
        if (reg == null)
        {
            throw new IllegalArgumentException("Unregistered message " + message.getClass().getName());
        }
        FriendlyByteBuf buf = PacketByteBufs.create();
        ((BiConsumer<MSG, FriendlyByteBuf>) reg.encoder).accept(message, buf);
        ServerPlayNetworking.send(player, idFor(message), buf);
    }

    public void send(com.wintercogs.beyonddimensions.forgecompat.network.PacketDistributor.PacketTarget target, Object message)
    {
        target.send(this, message);
    }

    public void init()
    {
        // C2S 与双向消息在服务端注册
        for (Map.Entry<Class<?>, Registration> entry : registrations.entrySet())
        {
            Registration reg = entry.getValue();
            if (reg.direction == NetworkDirection.PLAY_TO_CLIENT)
            {
                continue;
            }
            ResourceLocation id = new ResourceLocation(channelId.getNamespace(), channelId.getPath() + "_" + entry.getKey().getSimpleName().toLowerCase(java.util.Locale.ROOT));
            ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, responseSender) ->
            {
                Object msg = reg.decoder.apply(buf);
                server.execute(() ->
                {
                    NetworkEvent.Context context = new NetworkEvent.Context(player, NetworkDirection.PLAY_TO_SERVER);
                    ((BiConsumer<Object, Supplier<NetworkEvent.Context>>) reg.handler).accept(msg, () -> context);
                });
            });
        }
    }

    public void initClient()
    {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT)
        {
            return;
        }
        for (Map.Entry<Class<?>, Registration> entry : registrations.entrySet())
        {
            Registration reg = entry.getValue();
            if (reg.direction == NetworkDirection.PLAY_TO_SERVER)
            {
                continue;
            }
            ResourceLocation id = new ResourceLocation(channelId.getNamespace(), channelId.getPath() + "_" + entry.getKey().getSimpleName().toLowerCase(java.util.Locale.ROOT));
            ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) ->
            {
                Object msg = reg.decoder.apply(buf);
                client.execute(() ->
                {
                    NetworkEvent.Context context = new NetworkEvent.Context(null, NetworkDirection.PLAY_TO_CLIENT);
                    ((BiConsumer<Object, Supplier<NetworkEvent.Context>>) reg.handler).accept(msg, () -> context);
                });
            });
        }
    }

    private static class Registration
    {
        private final int id;
        private final BiConsumer<?, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, ?> decoder;
        private final BiConsumer<?, Supplier<NetworkEvent.Context>> handler;
        private final NetworkDirection direction;

        public Registration(int id, BiConsumer<?, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, ?> decoder, BiConsumer<?, Supplier<NetworkEvent.Context>> handler, NetworkDirection direction)
        {
            this.id = id;
            this.encoder = encoder;
            this.decoder = decoder;
            this.handler = handler;
            this.direction = direction;
        }
    }
}
