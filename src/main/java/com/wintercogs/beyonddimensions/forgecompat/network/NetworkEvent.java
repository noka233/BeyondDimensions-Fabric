package com.wintercogs.beyonddimensions.forgecompat.network;

import net.minecraft.server.level.ServerPlayer;

public class NetworkEvent
{
    public static class Context
    {
        private final ServerPlayer sender;
        private final NetworkDirection direction;
        private boolean packetHandled;

        public Context(ServerPlayer sender, NetworkDirection direction)
        {
            this.sender = sender;
            this.direction = direction;
        }

        public ServerPlayer getSender()
        {
            return sender;
        }

        public void enqueueWork(Runnable runnable)
        {
            runnable.run();
        }

        public void setPacketHandled(boolean packetHandled)
        {
            this.packetHandled = packetHandled;
        }

        public NetworkDirection getDirection()
        {
            return direction;
        }
    }
}
