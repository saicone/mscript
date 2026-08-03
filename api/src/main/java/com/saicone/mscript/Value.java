package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@FunctionalInterface
public interface Value<T> extends Section {

    Value<?> NULL = context -> null;
    Value<?> TRUE = context -> true;
    Value<?> FALSE = context -> false;

    @NotNull
    @SuppressWarnings("unchecked")
    static <T> Value<T> literal(@Nullable T value) {
        if (value == null) {
            return (Value<T>) NULL;
        } else if (Boolean.TRUE.equals(value)) {
            return (Value<T>) TRUE;
        } else if (Boolean.FALSE.equals(value)) {
            return (Value<T>) FALSE;
        }
        return context -> value;
    }

    @NotNull
    static Value<String> using(@NotNull String str) {
        return context -> context.parse(str);
    }

    @NotNull
    static <T> Value<T> using(@NotNull Function<Object, T> parser, @NotNull String str) {
        return context -> parser.apply(context.parse(str));
    }

    @UnknownNullability
    T get(@NotNull Context context);

    @NotNull
    default Value<Object> andThen(@NotNull Operator operator, @NotNull Value<T> b) {
        return context -> operator.eval(context, this, b);
    }
}
