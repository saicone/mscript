package com.saicone.mscript.util.cache;

import com.saicone.mscript.util.cache.caffeine.CaffeineCache;
import com.saicone.mscript.util.cache.guava.GuavaCache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Experimental
public interface Cache<K, V> {

    static boolean isAvailable() {
        try {
            Class.forName("com.github.benmanes.caffeine.cache.Caffeine");
            return true;
        } catch (ClassNotFoundException ignored) { }
        try {
            Class.forName("com.google.common.cache.CacheBuilder");
            return true;
        } catch (ClassNotFoundException ignored) { }
        return false;
    }

    @NotNull
    static <K, V> Builder<K, V> newBuilder() {
        try {
            Class.forName("com.github.benmanes.caffeine.cache.Caffeine");
            return new CaffeineCache.Builder<>();
        } catch (ClassNotFoundException ignored) { }
        try {
            Class.forName("com.google.common.cache.CacheBuilder");
            return new GuavaCache.Builder<>();
        } catch (ClassNotFoundException ignored) { }
        throw new IllegalStateException("There's no cache library loaded into current classpath");
    }

    default boolean isEmpty() {
        return estimatedSize() == 0;
    }

    @Nullable
    V getIfPresent(@NotNull K key);

    V get(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) throws ComputeFailure;

    @NotNull
    default Map<K, V> getAllPresent(@NotNull K... keys) {
        return getAllPresent(Set.of(keys));
    }

    @NotNull
    Map<K, V> getAllPresent(@NotNull Iterable<? extends K> keys);

    void put(@NotNull K key, @NotNull V value);

    void putAll(@NotNull Map<? extends K, ? extends V> map);

    void invalidate(@NotNull K key);

    void invalidateAll(@NotNull Iterable<? extends K> keys);

    void invalidateAll();

    long estimatedSize();

    @NotNull
    ConcurrentMap<K, V> asMap();

    @NotNull
    @SuppressWarnings("unchecked")
    default Set<K> asSet() {
        return Collections.newSetFromMap((Map<K, Boolean>) asMap());
    }

    void cleanUp();

    interface Builder<K, V> {

        @NotNull
        Builder<K, V> initialCapacity(int initialCapacity);

        @NotNull
        Builder<K, V> executor(@NotNull Executor executor);

        // Builder<K, V> scheduler(@NotNull Scheduler scheduler);

        @NotNull
        Builder<K, V> maximumSize(long maximumSize);

        @NotNull
        Builder<K, V> maximumWeight(long maximumWeight);

        @NotNull
        Builder<K, V> weigher(@NotNull BiFunction<K, V, @NotNull Integer> weigher);

        @NotNull
        default Builder<K, V> singletonWeigher() {
            return weigher((k, v) -> 1);
        }

        @NotNull
        default Builder<K, V> boundedWeigher(@NotNull BiFunction<K, V, @NotNull Integer> weigher) {
            return weigher((k, v) -> {
                int weight = weigher.apply(k, v);
                if (weight < 0) {
                    throw new IllegalArgumentException();
                }
                return weight;
            });
        }

        @NotNull
        Builder<K, V> weakKeys();

        @NotNull
        Builder<K, V> weakValues();

        @NotNull
        Builder<K, V> softValues();

        @NotNull
        Builder<K, V> expireAfterWrite(@NotNull Duration duration);

        @NotNull
        Builder<K, V> expireAfterWrite(long duration, @NotNull TimeUnit unit);

        @NotNull
        Builder<K, V> expireAfterAccess(@NotNull Duration duration);

        @NotNull
        Builder<K, V> expireAfterAccess(long duration, @NotNull TimeUnit unit);

        // <K1 extends K, V1 extends V> Builder<K, V> expireAfter(@NotNull Expiry<K1, V1> expiry);

        @NotNull
        Builder<K, V> refreshAfterWrite(@NotNull Duration duration);

        @NotNull
        Builder<K, V> refreshAfterWrite(long duration, @NotNull TimeUnit unit);

        @NotNull
        Builder<K, V> ticker(@NotNull Supplier<@NotNull Long> ticker);

        @NotNull
        default Builder<K, V> systemTicker() {
            return ticker(System::nanoTime);
        }

        @NotNull
        default Builder<K, V> disabledTicker() {
            return ticker(() -> 0L);
        }

        @NotNull
        default Builder<K, V> evictionListener(@NotNull EntryListener<K, V> evictionListener) {
            return removalListener((key, value, cause) -> {
                if (cause.wasEvicted()) {
                    evictionListener.onEntryChange(key, value, cause);
                }
            });
        }

        @NotNull
        Builder<K, V> removalListener(@NotNull EntryListener<K, V> removalListener);

        @NotNull
        Builder<K, V> recordStats();

        @NotNull
        Cache<K, V> build();

        @NotNull
        LoadingCache<K, V> build(@NotNull CacheLoader<K, V> loader);
    }

    final class ComputeFailure extends RuntimeException {
        final Throwable cause;

        public ComputeFailure(@NotNull Throwable cause) {
            super(null, cause, false, false);
            this.cause = cause;
        }
    }
}
