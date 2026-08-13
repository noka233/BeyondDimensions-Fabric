package com.wintercogs.beyonddimensions.client.command;

import com.mojang.brigadier.Command;
import com.wintercogs.beyonddimensions.util.TooltipHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ClientCommands
{
    public static void register()
    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(
                        net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("bdtools")
                                .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("searchCache")
                                        .then(net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal("clear")
                                                .executes(context -> {
                                                    TooltipHelper.clearCache();
                                                    context.getSource().sendFeedback(Component.literal("Tooltip cache cleared."));
                                                    return Command.SINGLE_SUCCESS;
                                                })))
                ));
    }
}
