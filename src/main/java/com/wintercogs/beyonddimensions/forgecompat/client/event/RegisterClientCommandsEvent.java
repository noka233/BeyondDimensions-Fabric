package com.wintercogs.beyonddimensions.forgecompat.client.event;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class RegisterClientCommandsEvent
{
    private final CommandDispatcher<FabricClientCommandSource> dispatcher;

    public RegisterClientCommandsEvent(CommandDispatcher<FabricClientCommandSource> dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public CommandDispatcher<FabricClientCommandSource> getDispatcher()
    {
        return dispatcher;
    }
}
