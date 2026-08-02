package com.saicone.mscript.context;

import com.saicone.mscript.ComposedContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class AbstractComposedContext implements ComposedContext {

    private Function<String, String> parser;
    private Map<String, Object> literals;
    private Map<String, Object> bindings;
    private Map<String, Object> attributes;

    @Override
    public void forEachBinding(@NotNull BiConsumer<String, Object> consumer) {
        if (this.bindings != null) {
            this.bindings.forEach(consumer);
        }
    }

    @Override
    public void forEachAttribute(@NotNull BiConsumer<String, Object> consumer) {
        if (this.attributes != null) {
            this.attributes.forEach(consumer);
        }
    }

    @Override
    public @NotNull String parse(@NotNull String str) {
        String result = str;
        if (this.parser != null) {
            result = this.parser.apply(result);
        }
        if (this.literals != null) {
            for (Map.Entry<String, Object> entry : this.literals.entrySet()) {
                result = result.replace(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return result;
    }

    @Override
    public @NotNull ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        if (this.parser == null) {
            this.parser = operator;
        } else {
            this.parser = this.parser.andThen(operator);
        }
        return this;
    }

    @Override
    public @NotNull ComposedContext replace(@NotNull String str, @NotNull Object value) {
        if (this.literals == null) {
            this.literals = new HashMap<>();
        }
        this.literals.put(str, value);
        return this;
    }

    @Override
    public @NotNull ComposedContext binding(@NotNull String key, @NotNull Object value) {
        if (this.bindings == null) {
            this.bindings = new HashMap<>();
        }
        this.bindings.put(key, value);
        return this;
    }

    @Override
    public @NotNull ComposedContext attribute(@NotNull String key, @NotNull Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
        return this;
    }
}
