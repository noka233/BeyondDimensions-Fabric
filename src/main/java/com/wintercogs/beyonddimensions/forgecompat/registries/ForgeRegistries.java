package com.wintercogs.beyonddimensions.forgecompat.registries;

import com.wintercogs.beyonddimensions.forgecompat.fluids.FluidType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public class ForgeRegistries
{
    public static final Registry<Item> ITEMS = BuiltInRegistries.ITEM;
    public static final Registry<Block> BLOCKS = BuiltInRegistries.BLOCK;
    public static final Registry<Fluid> FLUIDS = BuiltInRegistries.FLUID;
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPES = BuiltInRegistries.BLOCK_ENTITY_TYPE;

    public static class Keys
    {
        public static final ResourceKey<Registry<Fluid>> FLUIDS = Registries.FLUID;
        public static final ResourceKey<Registry<FluidType>> FLUID_TYPES = ResourceKey.createRegistryKey(new ResourceLocation("beyonddimensions", "fluid_type"));
    }
}
