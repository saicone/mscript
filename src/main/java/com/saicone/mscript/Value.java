package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@FunctionalInterface
public interface Value<T> extends Section {

    @NotNull
    static <T> Value<T> of(@NotNull T value) {
        return context -> value;
    }

    @NotNull
    static <T> Value<T> of(@NotNull Function<Object, T> parser, @NotNull String str) {
        return context -> parser.apply(context.parse(str));
    }

    @UnknownNullability
    T get(@NotNull Context context);

    @NotNull
    default Value<Object> andThen(@NotNull Operator operator, @NotNull Value<T> b) {
        return context -> operator.eval(context, this, b);
    }
}
