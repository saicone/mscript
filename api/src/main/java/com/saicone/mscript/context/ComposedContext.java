package com.saicone.mscript.context;

import com.saicone.mscript.Context;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ComposedContext extends DelegateContext {

    private Function<String, String> parser;
    private Map<String, Object> literals;
    private Map<String, Object> bindings;
    private Map<String, Object> attributes;

    public ComposedContext(@NotNull Context delegate) {
        super(delegate);
    }

    @NotNull
    public Map<String, Object> literals() {
        return literals == null ? Map.of() : literals;
    }

    @NotNull
    public Map<String, Object> bindings() {
        return bindings == null ? Map.of() : bindings;
    }

    @NotNull
    public Map<String, Object> attributes() {
        return attributes == null ? Map.of() : attributes;
    }

    @Override
    public @NotNull String parse(@NotNull String str) {
        String result = super.parse(str);
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

    @NotNull
    @Contract("_ -> this")
    public ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        if (this.parser == null) {
            this.parser = operator;
        } else {
            this.parser = this.parser.andThen(operator);
        }
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public ComposedContext replace(@NotNull String str, @NotNull Object value) {
        if (this.literals == null) {
            this.literals = new HashMap<>();
        }
        this.literals.put(str, value);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public ComposedContext binding(@NotNull String key, @NotNull Object value) {
        if (this.bindings == null) {
            this.bindings = new HashMap<>();
        }
        this.bindings.put(key, value);
        return this;
    }

    @NotNull
    @Contract("_, _ -> this")
    public ComposedContext attribute(@NotNull String key, @NotNull Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
        return this;
    }

    @Override
    public @NotNull ComposedContext composed() {
        return this;
    }
}
