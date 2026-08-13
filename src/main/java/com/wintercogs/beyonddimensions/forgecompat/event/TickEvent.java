package com.wintercogs.beyonddimensions.forgecompat.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class TickEvent
{
    public enum Phase
    {
        START, END
    }

    public static class ServerTickEvent
    {
        public final MinecraftServer server;

        public ServerTickEvent(MinecraftServer server)
        {
            this.server = server;
        }
    }

    public static class ClientTickEvent
    {
        public final Minecraft client;
        public final Phase phase;

        public ClientTickEvent(Minecraft client, Phase phase)
        {
            this.client = client;
            this.phase = phase;
        }
    }
}
