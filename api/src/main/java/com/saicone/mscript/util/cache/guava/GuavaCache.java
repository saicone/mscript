package com.saicone.mscript.util.cache.guava;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.saicone.mscript.util.cache.CacheLoader;
import com.saicone.mscript.util.cache.EntryListener;
import com.saicone.mscript.util.cache.LoadingCache;
import com.saicone.mscript.util.cache.Cache;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class GuavaCache<K, V> implements Cache<K, V> {

    private final com.google.common.cache.Cache<K, V> cache;

    public GuavaCache(@NotNull com.google.common.cache.Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public @Nullable V getIfPresent(@NotNull K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public V get(@NotNull K key, @NotNull Function<? super K, ? extends V> mappingFunction) throws ComputeFailure {
        try {
            return cache.get(key, () -> mappingFunction.apply(key));
        } catch (ExecutionException | ExecutionError e) {
            throw new ComputeFailure(e.getCause());
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
        return cache.size();
    }

    @Override
    public @NotNull ConcurrentMap<K, V> asMap() {
        return cache.asMap();
    }

    @Override
    public void cleanUp() {
        cache.cleanUp();
    }

    public static class Loading<K, V> extends GuavaCache<K, V> implements LoadingCache<K, V> {

        private final com.google.common.cache.LoadingCache<K, V> cache;

        public Loading(@NotNull com.google.common.cache.LoadingCache<K, V> cache) {
            super(cache);
            this.cache = cache;
        }

        @Override
        public V get(K key) {
            return cache.getUnchecked(key);
        }

        @Override
        public @NotNull Map<K, V> getAll(Iterable<? extends K> keys) {
            try {
                return cache.getAll(keys);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public @NotNull CompletableFuture<V> refresh(K key) {
            cache.refresh(key);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull CompletableFuture<Map<K, V>> refreshAll(Iterable<? extends K> keys) {
            for (K key : keys) {
                cache.refresh(key);
            }
            return CompletableFuture.completedFuture(null);
        }
    }

    public static class Builder<K, V> implements Cache.Builder<K, V> {

        private final CacheBuilder<Object, Object> builder;

        private Executor executor;

        public Builder() {
            this(CacheBuilder.newBuilder());
        }

        public Builder(@NotNull CacheBuilder<Object, Object> builder) {
            this.builder = builder;
        }

        @Override
        public @NotNull Cache.Builder<K, V> initialCapacity(int initialCapacity) {
            this.builder.initialCapacity(initialCapacity);
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> executor(@NotNull Executor executor) {
            this.executor = executor;
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
            this.builder.ticker(new Ticker() {
                @Override
                public long read() {
                    return ticker.get();
                }
            });
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> systemTicker() {
            this.builder.ticker(Ticker.systemTicker());
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> removalListener(@NotNull EntryListener<K, V> removalListener) {
            this.builder.<K, V>removalListener(notification -> removalListener.onEntryChange(notification.getKey(), notification.getValue(), EntryListener.Cause.of(notification.getCause())));
            return this;
        }

        @Override
        public @NotNull Cache.Builder<K, V> recordStats() {
            this.builder.recordStats();
            return this;
        }

        @Override
        public @NotNull Cache<K, V> build() {
            return new GuavaCache<>(this.builder.build());
        }

        @Override
        public @NotNull LoadingCache<K, V> build(@NotNull CacheLoader<K, V> loader) {
            final com.google.common.cache.CacheLoader<K, V> cacheLoader = new com.google.common.cache.CacheLoader<>() {
                @Override
                public @NotNull V load(@NotNull K key) throws Exception {
                    return Objects.requireNonNull(loader.load(key));
                }

                @Override
                public @NotNull Map<K, V> loadAll(@NotNull Iterable<? extends K> keys) throws Exception {
                    return loader.loadAll(keys);
                }

                @Override
                public @NotNull ListenableFuture<V> reload(@NotNull K key, @NotNull V oldValue) throws Exception {
                    return Futures.immediateFuture(loader.reload(key, oldValue));
                }
            };
            return new Loading<>(this.builder.build(this.executor == null ? cacheLoader : com.google.common.cache.CacheLoader.asyncReloading(cacheLoader, this.executor)));
        }
    }
}
