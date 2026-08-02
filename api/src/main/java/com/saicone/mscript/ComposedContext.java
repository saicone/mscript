package com.saicone.mscript;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface ComposedContext extends Context {

    void forEachBinding(@NotNull BiConsumer<String, Object> consumer);

    void forEachAttribute(@NotNull BiConsumer<String, Object> consumer);

    @NotNull
    @Contract("_ -> this")
    default ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        throw new IllegalStateException("The current context does not support parser override, use #composed() method instead");
    }

    @NotNull
    @Contract("_, _ -> this")
    default ComposedContext replace(@NotNull String str, @NotNull Object value) {
        throw new IllegalStateException("The current context does not support literal replacement, use #composed() method instead");
    }

    @NotNull
    @Contract("_, _ -> this")
    ComposedContext binding(@NotNull String key, @NotNull Object value);

    @NotNull
    @Contract("_, _ -> this")
    ComposedContext attribute(@NotNull String key, @NotNull Object value);

    @NotNull
    @Contract("_, _ -> this")
    default ComposedContext function(@NotNull String key, @NotNull Function<Object, Object> function) {
        return attribute(key, function);
    }
}
