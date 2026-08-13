package com.wintercogs.beyonddimensions.forgecompat.eventbus.api;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class IEventBus
{
    private final Map<Class<?>, List<Consumer<Object>>> listeners = new HashMap<>();

    public <T> void addListener(Consumer<T> consumer)
    {
        addListener(EventPriority.NORMAL, consumer);
    }

    public <T> void addListener(EventPriority priority, Consumer<T> consumer)
    {
        listeners.computeIfAbsent(findEventType(consumer), k -> new ArrayList<>()).add(e -> consumer.accept((T) e));
    }

    public void register(Object container)
    {
        Class<?> cls = container instanceof Class<?> c ? c : container.getClass();
        Object instance = container instanceof Class<?> ? null : container;
        for (Method method : cls.getDeclaredMethods())
        {
            if (!method.isAnnotationPresent(SubscribeEvent.class) || method.getParameterCount() != 1)
            {
                continue;
            }
            Class<?> eventType = method.getParameterTypes()[0];
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (!isStatic && instance == null)
            {
                continue;
            }
            method.setAccessible(true);
            listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(e ->
            {
                try
                {
                    method.invoke(isStatic ? null : instance, e);
                }
                catch (Exception ex)
                {
                    throw new RuntimeException("Failed to dispatch event " + eventType.getName(), ex);
                }
            });
        }
    }

    public void post(Object event)
    {
        List<Consumer<Object>> list = listeners.get(event.getClass());
        if (list != null)
        {
            for (Consumer<Object> consumer : new ArrayList<>(list))
            {
                consumer.accept(event);
            }
        }
    }

    private static <T> Class<?> findEventType(Consumer<T> consumer)
    {
        Class<?> cls = consumer.getClass();
        for (java.lang.reflect.Type genericInterface : cls.getGenericInterfaces())
        {
            if (genericInterface instanceof java.lang.reflect.ParameterizedType pt && pt.getRawType() == Consumer.class)
            {
                java.lang.reflect.Type arg = pt.getActualTypeArguments()[0];
                if (arg instanceof Class<?> c)
                {
                    return c;
                }
            }
        }
        return Object.class;
    }
}
