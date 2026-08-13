package com.wintercogs.beyonddimensions.forgecompat.registries;

import java.util.function.Supplier;

public class RegistryObject<T> implements Supplier<T>
{
    private final String name;
    private final Supplier<? extends T> supplier;
    private T value;

    RegistryObject(String name, Supplier<? extends T> supplier)
    {
        this.name = name;
        this.supplier = supplier;
    }

    public T get()
    {
        if (value == null)
        {
            value = supplier.get();
        }
        return value;
    }

    public String getName()
    {
        return name;
    }

    void setValue(Object value)
    {
        this.value = (T) value;
    }
}
