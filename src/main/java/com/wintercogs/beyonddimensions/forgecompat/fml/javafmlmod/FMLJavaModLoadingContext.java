package com.wintercogs.beyonddimensions.forgecompat.fml.javafmlmod;

import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.IEventBus;

public class FMLJavaModLoadingContext
{
    private static final IEventBus MOD_EVENT_BUS = new IEventBus();

    public static FMLJavaModLoadingContext get()
    {
        return new FMLJavaModLoadingContext();
    }

    public IEventBus getModEventBus()
    {
        return MOD_EVENT_BUS;
    }
}
