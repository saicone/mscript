package com.saicone.mscript.util.cache;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
@ApiStatus.Experimental
public interface EntryListener<K, V> {

    void onEntryChange(@Nullable K key, @Nullable V value, @NotNull Cause cause);

    enum Cause {
        EXPLICIT{
            @Override
            public boolean wasEvicted() {
                return false;
            }
        },
        REPLACED{
            @Override
            public boolean wasEvicted() {
                return false;
            }
        },
        COLLECTED,
        EXPIRED,
        SIZE;

        private static final Cause[] VALUES = values();

        public boolean wasEvicted() {
            return true;
        }

        @NotNull
        public static <E extends Enum<E>> Cause of(@NotNull E e) {
            return VALUES[e.ordinal()];
        }
    }
}
