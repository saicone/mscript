package com.saicone.mscript.util.cache;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@ApiStatus.Experimental
public interface CacheLoader<K, V> {

    @Nullable
    V load(@NotNull K key) throws Exception;

    @Nullable
    default V reload(@NotNull K key, @NotNull V oldValue) throws Exception {
        return load(key);
    }

    default Map<K, V> loadAll(@NotNull Iterable<? extends K> keys) throws Exception {
        throw new UnsupportedOperationException();
    }
}
