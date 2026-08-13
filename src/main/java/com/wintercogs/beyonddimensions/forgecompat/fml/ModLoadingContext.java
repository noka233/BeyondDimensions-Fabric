package com.wintercogs.beyonddimensions.forgecompat.fml;

public class ModLoadingContext
{
    private static final ModLoadingContext INSTANCE = new ModLoadingContext();

    public static ModLoadingContext get()
    {
        return INSTANCE;
    }

    public void registerConfig(Object type, Object spec)
    {
    }
}
