package com.wintercogs.beyonddimensions.forgecompat.event.server;

import net.minecraft.server.MinecraftServer;

public class ServerStartingEvent
{
    private final MinecraftServer server;

    public ServerStartingEvent(MinecraftServer server)
    {
        this.server = server;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
