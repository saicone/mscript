package com.saicone.mscript.context;

import com.saicone.mscript.Context;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public class DelegateContext implements Context {

    private final Context delegate;

    public DelegateContext(@NotNull Context delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull Object source() {
        return delegate.source();
    }

    @Override
    public @Nullable Object agent() {
        return delegate.agent();
    }

    @Override
    public @NotNull Audience audience() {
        return delegate.audience();
    }

    @Override
    public @NotNull <T> T get() {
        return delegate.get();
    }

    @Override
    public @NotNull <T> T get(@NotNull Class<T> type) {
        return delegate.get(type);
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return delegate.getUniqueId();
    }

    @Override
    public @NotNull String parse(@NotNull String str) {
        return delegate.parse(str);
    }

    @Override
    public @Nullable Component parse(@Nullable Component component) {
        return delegate.parse(component);
    }

    @Override
    public void sync(@NotNull Runnable command) {
        delegate.sync(command);
    }

    @Override
    public void async(@NotNull Runnable command) {
        delegate.async(command);
    }

    @Override
    public void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        delegate.delay(time, unit, command);
    }

    @Override
    public <A extends Context> @NotNull A as(@NotNull Class<A> type) {
        return delegate.as(type);
    }
}
