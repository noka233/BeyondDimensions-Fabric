package com.wintercogs.beyonddimensions.integration.module.polymorph;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class PolymorphHelper
{
    public static Optional<CraftingRecipe> getRecipe(Player player, RecipeType<?> recipeType, CraftingContainer input, Level level)
    {
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
    }
}
