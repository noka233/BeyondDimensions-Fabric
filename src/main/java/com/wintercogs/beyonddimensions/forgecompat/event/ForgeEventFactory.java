package com.wintercogs.beyonddimensions.forgecompat.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ForgeEventFactory
{
    public static ItemStack onItemUseFinish(Player player, ItemStack stack, int duration, ItemStack result)
    {
        return result;
    }

    public static void firePlayerItemPickupEvent(Player player, ItemEntity item, ItemStack stack)
    {
    }
}
