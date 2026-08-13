package com.wintercogs.beyonddimensions.forgecompat.network;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class NetworkHooks
{
    public static void openScreen(ServerPlayer player, MenuProvider provider)
    {
        openScreen(player, provider, (Consumer<FriendlyByteBuf>) null);
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, BlockPos pos)
    {
        openScreen(player, provider, buf -> buf.writeBlockPos(pos));
    }

    public static void openScreen(ServerPlayer player, MenuProvider provider, Consumer<FriendlyByteBuf> extraData)
    {
        player.openMenu(new ExtendedScreenHandlerFactory()
        {
            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf)
            {
                if (extraData != null)
                {
                    extraData.accept(buf);
                }
            }

            @Override
            public Component getDisplayName()
            {
                return provider.getDisplayName();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory inv, Player player)
            {
                return provider.createMenu(id, inv, player);
            }
        });
    }
}
