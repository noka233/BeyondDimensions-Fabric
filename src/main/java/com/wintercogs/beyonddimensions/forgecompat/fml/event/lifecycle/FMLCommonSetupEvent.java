package com.wintercogs.beyonddimensions.forgecompat.fml.event.lifecycle;

public class FMLCommonSetupEvent
{
    public void enqueueWork(Runnable runnable)
    {
        runnable.run();
    }
}
