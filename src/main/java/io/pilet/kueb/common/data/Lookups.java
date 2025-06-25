package io.pilet.kueb.common.data;

import java.util.HashMap;
import java.util.Map;

public class Lookups {
    private static final Map<Class<?>,Lookup<?>> lookupMap = new HashMap<>();

    public static <T> void register(Class<T> tClass, Lookup<T> lookup) {
        register(tClass, lookup, false);
    }

    public static <T> void register(Class<T> tClass, Lookup<T> lookup, boolean cached) {
        Lookup<T> toRegister = (cached) ? new CachedLookup<>(lookup) : lookup;
        lookupMap.put(tClass, toRegister);
    }

    public static <T> Lookup<T> of(Class<T> tClass) {
        return (Lookup<T>) lookupMap.get(tClass);
    }
}
