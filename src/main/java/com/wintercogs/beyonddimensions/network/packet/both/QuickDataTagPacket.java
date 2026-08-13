package com.wintercogs.beyonddimensions.network.packet.both;

import com.wintercogs.beyonddimensions.common.menu.BDBaseMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import com.wintercogs.beyonddimensions.forgecompat.api.distmarker.Dist;
import com.wintercogs.beyonddimensions.forgecompat.fml.DistExecutor;
import com.wintercogs.beyonddimensions.forgecompat.network.NetworkDirection;
import com.wintercogs.beyonddimensions.forgecompat.network.NetworkEvent;

import java.util.function.Supplier;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

public record QuickDataTagPacket(CompoundTag tag)
{
    private void handleServer(NetworkEvent.Context context)
    {
        Player player = context.getSender();
        if (player.containerMenu instanceof BDBaseMenu menu)
        {
            menu.readQuickDataTag(tag());
        }
    }

    @Environment(EnvType.CLIENT)
    private void handleClient(NetworkEvent.Context context)
    {
        Player player = Minecraft.getInstance().player;
        if (player.containerMenu instanceof BDBaseMenu menu)
        {
            menu.readQuickDataTag(tag());
        }
    }


    public static void handle(QuickDataTagPacket packet, Supplier<NetworkEvent.Context> cxt)
    {
        if (packet != null)
        {
            NetworkEvent.Context context = cxt.get();
            NetworkDirection direction = context.getDirection();
            if (direction == NetworkDirection.PLAY_TO_CLIENT)
            {
                context.enqueueWork(() ->
                        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> packet.handleClient(context))
                );
                context.setPacketHandled(true);
            }
            else if (direction == NetworkDirection.PLAY_TO_SERVER)
            {
                context.enqueueWork(() -> packet.handleServer(context));
                context.setPacketHandled(true);
            }
        }
    }

    public static void encode(QuickDataTagPacket packet, FriendlyByteBuf buf)
    {
        buf.writeNbt(packet.tag);
    }

    public static QuickDataTagPacket decode(FriendlyByteBuf buf)
    {
        CompoundTag tag = buf.readNbt();

        return new QuickDataTagPacket(tag);
    }
}
