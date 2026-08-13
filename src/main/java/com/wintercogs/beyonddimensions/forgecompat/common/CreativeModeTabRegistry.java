package com.wintercogs.beyonddimensions.forgecompat.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class CreativeModeTabRegistry
{
    public static List<CreativeModeTab> getSortedCreativeModeTabs()
    {
        return BuiltInRegistries.CREATIVE_MODE_TAB.stream().toList();
    }
}
