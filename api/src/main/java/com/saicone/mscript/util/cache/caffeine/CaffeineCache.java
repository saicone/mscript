package com.saicone.mscript.util.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.Weigher;
import com.saicone.mscript.util.cache.CacheLoader;
import com.saicone.mscript.util.cache.EntryListener;
import com.saicone.mscript.util.cache.LoadingCache;
import com.saicone.mscript.util.cache.Cache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    public CaffeineCache(@NotNull com.github.benmanes.caffeine.cache.Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public @Nullable V getIfPresent(@NotNull K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public V get(@NonNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) throws ComputeFailure {
        try {
            return cache.get(key, mappingFunction);
        } catch (Throwable t) {
            throw new ComputeFailure(t);
        }
    }

    @Override
    public @NotNull Map<K, V> getAllPresent(@NotNull Iterable<? extends K> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public void put(@NotNull K key, @NotNull V value) {
        cache.put(key, value);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    @Override
    public void invalidate(@NotNull K key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(@NotNull Iterable<? extends K> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        cache.invalidateAll();
    }

    @Override
    public long estimatedSize() {
        return cache.estimatedSize();
    }

    @Override
    public @NotNull ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    public static class Loading<K, V> extends CaffeineCache<K, V> implements LoadingCache<K, V> {

        private final com.github.benmanes.caffeine.cache.LoadingCache<K, V> cache;

        public Loading(@NotNull com.github.benmanes.caffeine.cache.LoadingCache<K, V> cache) {
            super(cache);
            this.cache = cache;
        }

        @Override
        public V get(K key) {
            return cache.get(key);
        }

        @Override
        public @NotNull Map<K, V> getAll(Iterable<? extends K> keys) {
            return cache.getAll(keys);
        }

        @Override
        public @NotNull CompletableFuture<V> refresh(K key) {
            return cache.refresh(key);
        }

        @Override
        public @NotNull CompletableFuture<Map<K, V>> refreshAll(Iterable<? extends K> keys) {
            return cache.refreshAll(keys);
        }
    }

    public static class Builder<K, V> implements Cache.Builder<K, V> {

        private final Caffeine<Object, Object> builder;

        public Builder() {
            this(Caffeine.newBuilder());
        }

        public Builder(@NotNull Caffeine<Object, Object> builder) {
            this.builder = builder;
        }

        @Override
        public @NotNull Cache.Builder<K, V> initialCapacity(int initialCapacity) {
            this.builder.initialCapacity(initialCapacity);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> executor(@NotNull Executor executor) {
            this.builder.executor(executor);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> maximumSize(long maximumSize) {
            this.builder.maximumSize(maximumSize);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> maximumWeight(long maximumWeight) {
            this.builder.maximumWeight(maximumWeight);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> weigher(@NotNull BiFunction<K, V, @NotNull Integer> weigher) {
            this.builder.weigher(weigher::apply);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> singletonWeigher() {
            this.builder.weigher(Weigher.singletonWeigher());
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> boundedWeigher(@NotNull BiFunction<K, V, @NotNull Integer> weigher) {
            this.builder.weigher(Weigher.boundedWeigher(weigher::apply));
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> weakKeys() {
            this.builder.weakKeys();
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> weakValues() {
            this.builder.weakValues();
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> softValues() {
            this.builder.softValues();
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> expireAfterWrite(@NotNull Duration duration) {
            this.builder.expireAfterWrite(duration);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> expireAfterWrite(long duration, @NotNull TimeUnit unit) {
            this.builder.expireAfterWrite(duration, unit);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> expireAfterAccess(@NotNull Duration duration) {
            this.builder.expireAfterAccess(duration);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> expireAfterAccess(long duration, @NotNull TimeUnit unit) {
            this.builder.expireAfterAccess(duration, unit);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> refreshAfterWrite(@NotNull Duration duration) {
            this.builder.refreshAfterWrite(duration);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> refreshAfterWrite(long duration, @NotNull TimeUnit unit) {
            this.builder.refreshAfterWrite(duration, unit);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> ticker(@NotNull Supplier<@NotNull Long> ticker) {
            this.builder.ticker(ticker::get);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> systemTicker() {
            this.builder.ticker(Ticker.systemTicker());
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> disabledTicker() {
            this.builder.ticker(Ticker.disabledTicker());
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> evictionListener(@NotNull EntryListener<K, V> evictionListener) {
            this.builder.<K, V>evictionListener((key, value, cause) -> evictionListener.onEntryChange(key, value, EntryListener.Cause.of(cause)));
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> removalListener(@NotNull EntryListener<K, V> removalListener) {
            this.builder.<K, V>removalListener((key, value, cause) -> removalListener.onEntryChange(key, value, EntryListener.Cause.of(cause)));
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> recordStats() {
            this.builder.recordStats();
            return this;
        }

        @Override
        public @NotNull Cache<K, V> build() {
            return new CaffeineCache<>(this.builder.build());
        }

        @Override
        public @NotNull LoadingCache<K, V> build(@NotNull CacheLoader<K, V> loader) {
            return new Loading<>(this.builder.build(new com.github.benmanes.caffeine.cache.CacheLoader<>() {
                @Override
                public @Nullable V load(@NotNull K key) throws Exception {
                    return loader.load(key);
                }

                @Override
                public @NotNull Map<? extends K, ? extends V> loadAll(@NotNull Set<? extends K> keys) throws Exception {
                    return loader.loadAll(keys);
                }

                @Override
                public @Nullable V reload(@NotNull K key, @NotNull V oldValue) throws Exception {
                    return loader.reload(key, oldValue);
                }
            }));
        }
    }
}
