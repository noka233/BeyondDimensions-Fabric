package com.wintercogs.beyonddimensions.forgecompat.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyOptional<T>
{
    private final Supplier<T> supplier;
    private T value;
    private boolean resolved;
    private boolean invalid;
    private final List<Consumer<LazyOptional<T>>> listeners = new ArrayList<>();

    private LazyOptional(Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    public static <T> LazyOptional<T> of(Supplier<T> supplier)
    {
        return new LazyOptional<>(supplier);
    }

    public static <T> LazyOptional<T> ofNullable(T value)
    {
        return value == null ? empty() : of(() -> value);
    }

    public static <T> LazyOptional<T> empty()
    {
        return new LazyOptional<>(null);
    }

    public <X> LazyOptional<X> cast()
    {
        return (LazyOptional<X>) this;
    }

    public Optional<T> resolve()
    {
        if (invalid || supplier == null)
        {
            return Optional.empty();
        }
        if (!resolved)
        {
            value = supplier.get();
            resolved = true;
        }
        return Optional.ofNullable(value);
    }

    public boolean isPresent()
    {
        return !invalid && supplier != null && resolve().isPresent();
    }

    public void invalidate()
    {
        invalid = true;
        for (Consumer<LazyOptional<T>> listener : new ArrayList<>(listeners))
        {
            listener.accept(this);
        }
    }

    public void addListener(Consumer<LazyOptional<T>> listener)
    {
        listeners.add(listener);
    }

    public void ifPresent(Consumer<? super T> consumer)
    {
        resolve().ifPresent(consumer);
    }
}
