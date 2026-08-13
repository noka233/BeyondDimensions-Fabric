package com.wintercogs.beyonddimensions.forgecompat.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class RegisterCommandsEvent
{
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final Commands.CommandSelection environment;

    public RegisterCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment)
    {
        this.dispatcher = dispatcher;
        this.environment = environment;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }

    public Commands.CommandSelection getEnvironment()
    {
        return environment;
    }
}
