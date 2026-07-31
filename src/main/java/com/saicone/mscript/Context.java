package com.saicone.mscript;

import com.saicone.mscript.context.ComposedContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public interface Context {

    @NotNull
    Object source();

    @Nullable
    Object agent();

    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T get() {
        final Object agent = agent();
        if (agent != null) {
            return (T) agent;
        }
        return (T) source();
    }

    @NotNull
    default <T> T get(@NotNull Class<T> type) {
        final Object object = get();
        return type.cast(object);
    }

    @NotNull
    default Audience audience() {
        return (Audience) source();
    }

    @NotNull
    default String parse(@NotNull String str) {
        return str;
    }

    @Nullable
    @Contract("!null -> !null")
    default Component parse(@Nullable Component component) {
        if (component == null) {
            return null;
        }

        if (component instanceof TextComponent) {
            final String content = ((TextComponent) component).content();
            final String parsed = parse(content);
            if (!content.equals(parsed)) {
                component = ((TextComponent) component).content(parsed);
            }
        }

        if (component.children().isEmpty()) {
            return component;
        }

        return component.children(component.children().stream().map(this::parse).toList());
    }

    default void sync(@NotNull Runnable command) {
        command.run();
    }

    default void async(@NotNull Runnable command) {
        command.run();
    }

    default void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        throw new UnsupportedOperationException("Delayed execution is not supported in this context");
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default <A extends Context> A as(@NotNull Class<A> type) {
        return (A) this;
    }

    @NotNull
    default ComposedContext composed() {
        return new ComposedContext(this);
    }
}
