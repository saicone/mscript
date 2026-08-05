package com.saicone.mscript.context;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@ApiStatus.Internal
public interface DelegateContext extends Context {

    @NotNull
    Context delegate();

    @Override
    default @NotNull Object source() {
        return delegate().source();
    }

    @Override
    default @Nullable Object agent() {
        return delegate().agent();
    }

    @Override
    default @NotNull Audience audience() {
        return delegate().audience();
    }

    @Override
    @NotNull
    default Audience pointer() {
        return delegate().pointer();
    }

    @Override
    default @NotNull <T> T get() {
        return delegate().get();
    }

    @Override
    default @NotNull <T> T get(@NotNull Class<T> type) {
        return delegate().get(type);
    }

    @Override
    default @NotNull <T> Optional<T> getIf(@NotNull Class<T> type) {
        return delegate().getIf(type);
    }

    @Override
    default @NotNull UUID getUniqueId() {
        return delegate().getUniqueId();
    }

    @Override
    default @NotNull String parse(@NotNull String str) {
        return delegate().parse(str);
    }

    @Override
    default @Nullable Component parse(@Nullable Component component) {
        return delegate().parse(component);
    }

    @Override
    default void sync(@NotNull Runnable command) {
        delegate().sync(command);
    }

    @Override
    default void async(@NotNull Runnable command) {
        delegate().async(command);
    }

    @Override
    default void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        delegate().delay(time, unit, command);
    }

    @Override
    default <A extends Context> @NotNull A as(@NotNull Class<A> type) {
        return delegate().as(type);
    }

    @Override
    default @NotNull ComposedContext composed() {
        return delegate().composed();
    }
}
