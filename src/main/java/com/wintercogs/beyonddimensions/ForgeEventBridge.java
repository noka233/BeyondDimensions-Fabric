package com.wintercogs.beyonddimensions;

import com.wintercogs.beyonddimensions.forgecompat.common.MinecraftForge;
import com.wintercogs.beyonddimensions.forgecompat.event.RegisterCommandsEvent;
import com.wintercogs.beyonddimensions.forgecompat.event.TickEvent;
import com.wintercogs.beyonddimensions.forgecompat.event.server.ServerStartedEvent;
import com.wintercogs.beyonddimensions.forgecompat.event.server.ServerStartingEvent;
import com.wintercogs.beyonddimensions.forgecompat.server.ServerLifecycleHooks;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ForgeEventBridge
{
    private static boolean initialized;
    private static boolean clientInitialized;

    public static void init()
    {
        if (initialized)
        {
            return;
        }
        initialized = true;

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
        {
            ServerLifecycleHooks.setCurrentServer(server);
            MinecraftForge.EVENT_BUS.post(new ServerStartingEvent(server));
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
        {
            ServerLifecycleHooks.setCurrentServer(server);
            MinecraftForge.EVENT_BUS.post(new ServerStartedEvent(server));
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ServerLifecycleHooks.setCurrentServer(null));
        ServerTickEvents.END_SERVER_TICK.register(server -> MinecraftForge.EVENT_BUS.post(new TickEvent.ServerTickEvent(server)));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                MinecraftForge.EVENT_BUS.post(new RegisterCommandsEvent(dispatcher, environment)));
    }

    public static void initClient()
    {
        if (clientInitialized)
        {
            return;
        }
        clientInitialized = true;

        ClientTickEvents.END_CLIENT_TICK.register(client ->
                MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(client, TickEvent.Phase.END)));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                MinecraftForge.EVENT_BUS.post(new com.wintercogs.beyonddimensions.forgecompat.client.event.RegisterClientCommandsEvent(dispatcher)));
    }
}
