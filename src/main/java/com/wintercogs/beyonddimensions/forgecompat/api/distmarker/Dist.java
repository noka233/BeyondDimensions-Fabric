package com.wintercogs.beyonddimensions.forgecompat.api.distmarker;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public enum Dist
{
    CLIENT,
    DEDICATED_SERVER;

    public static Dist fromEnvironment()
    {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? CLIENT : DEDICATED_SERVER;
    }
}
