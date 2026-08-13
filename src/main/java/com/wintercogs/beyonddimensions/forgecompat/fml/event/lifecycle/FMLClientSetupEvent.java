package com.wintercogs.beyonddimensions.forgecompat.fml.event.lifecycle;

public class FMLClientSetupEvent
{
    public void enqueueWork(Runnable runnable)
    {
        runnable.run();
    }
}
