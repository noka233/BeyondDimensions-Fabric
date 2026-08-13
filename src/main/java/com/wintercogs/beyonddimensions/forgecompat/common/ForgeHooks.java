package com.wintercogs.beyonddimensions.forgecompat.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ForgeHooks
{
    public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType)
    {
        return AbstractFurnaceBlockEntity.getFuel().getOrDefault(stack.getItem(), 0);
    }

    public static void setCraftingPlayer(@Nullable Player player)
    {
    }
}
