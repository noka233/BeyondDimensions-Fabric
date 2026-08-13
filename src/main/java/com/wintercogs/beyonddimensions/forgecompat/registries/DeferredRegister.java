package com.wintercogs.beyonddimensions.forgecompat.registries;

import com.wintercogs.beyonddimensions.forgecompat.eventbus.api.IEventBus;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DeferredRegister<T>
{
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String modid;
    private final List<RegistryObject<? extends T>> objects = new ArrayList<>();
    private final Map<String, T> memoryRegistry = new HashMap<>();
    private boolean registered;

    private DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String modid)
    {
        this.registryKey = registryKey;
        this.modid = modid;
    }

    public static <T> DeferredRegister<T> create(Registry<T> registry, String modid)
    {
        return new DeferredRegister<>(registry.key(), modid);
    }

    public static <B> DeferredRegister<B> create(ResourceKey<? extends Registry<B>> key, String modid)
    {
        return new DeferredRegister<>(key, modid);
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> supplier)
    {
        RegistryObject<I> object = new RegistryObject<>(name, supplier);
        objects.add(object);
        return object;
    }

    @SuppressWarnings("unchecked")
    public void register(IEventBus eventBus)
    {
        if (registered)
        {
            return;
        }
        registered = true;
        for (RegistryObject<? extends T> object : objects)
        {
            T value = object.get();
            Registry<T> registry = getRegistry();
            if (registry != null)
            {
                Registry.register(registry, new ResourceLocation(modid, object.getName()), value);
            }
            else
            {
                memoryRegistry.put(object.getName(), value);
            }
            object.setValue(value);
        }
    }

    @SuppressWarnings("unchecked")
    private Registry<T> getRegistry()
    {
        if (registryKey.location().equals(Registries.ITEM.location()))
        {
            return (Registry<T>) BuiltInRegistries.ITEM;
        }
        if (registryKey.location().equals(Registries.BLOCK.location()))
        {
            return (Registry<T>) BuiltInRegistries.BLOCK;
        }
        if (registryKey.location().equals(Registries.FLUID.location()))
        {
            return (Registry<T>) BuiltInRegistries.FLUID;
        }
        if (registryKey.location().equals(Registries.BLOCK_ENTITY_TYPE.location()))
        {
            return (Registry<T>) BuiltInRegistries.BLOCK_ENTITY_TYPE;
        }
        if (registryKey.location().equals(Registries.MENU.location()))
        {
            return (Registry<T>) BuiltInRegistries.MENU;
        }
        if (registryKey.location().equals(Registries.CREATIVE_MODE_TAB.location()))
        {
            return (Registry<T>) BuiltInRegistries.CREATIVE_MODE_TAB;
        }
        return null;
    }

    public T getFromMemory(String name)
    {
        return memoryRegistry.get(name);
    }
}
