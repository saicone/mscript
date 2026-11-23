package com.saicone.mscript.util;

import com.saicone.mscript.util.cache.Cache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

@ApiStatus.Experimental
public class Memory<K, V> implements AutoCloseable {

    private final Predicate<K> predicate;
    private final Function<K, V> constructor;
    private final Cache<K, V> cache;

    public Memory(@NotNull Predicate<K> predicate, @NotNull Function<K, V> constructor, @NotNull Cache<K, V> cache) {
        this.predicate = predicate;
        this.constructor = constructor;
        this.cache = cache;
    }

    @NotNull
    public Predicate<? extends K> predicate() {
        return predicate;
    }

    @NotNull
    public Function<K, V> constructor() {
        return constructor;
    }

    @NotNull
    public Cache<K, V> cache() {
        return cache;
    }

    @NotNull
    public V run(@NotNull K key) throws Throwable {
        if (predicate.test(key)) {
            try {
                return cache.get(key, constructor);
            } catch (Cache.ComputeFailure e) {
                throw e.getCause();
            }
        } else {
            return constructor.apply(key);
        }
    }

    @Override
    public void close() throws Exception {
        cache.invalidateAll();
    }
}
