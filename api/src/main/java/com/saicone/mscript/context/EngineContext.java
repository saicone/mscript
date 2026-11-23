package com.saicone.mscript.context;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptContext;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents a context that can be used to evaluate a script.<br>
 * This context can store bindings and attributes.
 *
 * @author Rubenicos
 */
public interface EngineContext extends Context {

    /**
     * Get the {@link ScriptContext} representation of this context.
     *
     * @return a script context
     */
    @NotNull
    ScriptContext scriptContext();

    /**
     * Iterate hover every binding provided by this context.
     *
     * @param consumer the consumer to accept every binding
     */
    void forEachBinding(@NotNull BiConsumer<String, Object> consumer);

    /**
     * Iterate hover every attribute provided by this context.
     *
     * @param consumer the consumer to accept every attribute
     */
    void forEachAttribute(@NotNull BiConsumer<String, Object> consumer);

    /**
     * Assigns a binding to the context, which can be used to represent a literal variable or value in this context.
     *
     * @param key   the key of the binding, aka variable name
     * @param value the value of the binding, aka variable value
     * @return      this context
     */
    @NotNull
    @Contract("_, _ -> this")
    ComposedContext binding(@NotNull String key, @NotNull Object value);

    /**
     * Assigns an attribute to the context, which can be used to represent a scope-based variable or function.
     *
     * @param key   the key of the attribute
     * @param value the value of the attribute
     * @return      this context
     */
    @NotNull
    @Contract("_, _ -> this")
    ComposedContext attribute(@NotNull String key, @NotNull Object value);

    /**
     * Assigns a function to the context
     *
     * @param key      the key of the function
     * @param function the function to be assigned
     * @return         this context
     */
    @NotNull
    @Contract("_, _ -> this")
    default ComposedContext function(@NotNull String key, @NotNull Function<Object, Object> function) {
        return attribute(key, function);
    }
}
