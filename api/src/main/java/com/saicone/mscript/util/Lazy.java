package com.saicone.mscript.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Supplier;

@ApiStatus.Internal
public class Lazy<T> implements Supplier<T> {

    @NotNull
    public static <T> Lazy<T> of(@NotNull Supplier<T> initializer) {
        return new Lazy<>(initializer);
    }

    @NotNull
    public static <T> Lazy<T> ofFinal(@NotNull Supplier<T> initializer) {
        return new Lazy<>(initializer) {
            @Override
            public void set(T value) {
                throw new UnsupportedOperationException("Cannot set value of a final Lazy instance");
            }
        };
    }

    private final Supplier<T> initializer;
    private transient volatile boolean initialized;
    private transient T value;

    public Lazy(@NotNull Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    @UnknownNullability
    public T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    value = initializer.get();
                    initialized = true;
                }
            }
        }
        return value;
    }

    public void set(@UnknownNullability T value) {
        synchronized (this) {
            this.value = value;
            this.initialized = true;
        }
    }
}
