package com.wintercogs.beyonddimensions.forgecompat.fml;

import com.wintercogs.beyonddimensions.forgecompat.api.distmarker.Dist;

import java.util.function.Supplier;

public class DistExecutor
{
    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> runnableSupplier)
    {
        if (Dist.fromEnvironment() == dist)
        {
            runnableSupplier.get().run();
        }
    }
}
