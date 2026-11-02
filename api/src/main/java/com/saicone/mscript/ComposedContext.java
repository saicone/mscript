package com.saicone.mscript;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A composed context is generally used to delegate bindings and attributes
 * to another script processor in a language that can understand.<br>
 * Or in a simple way, to assign replacements to parse text values.
 *
 * @author Rubenicos
 */
public interface ComposedContext extends Context {

    void forEachBinding(@NotNull BiConsumer<String, Object> consumer);

    void forEachAttribute(@NotNull BiConsumer<String, Object> consumer);

    /**
     * Concatenates the operator to the parser of the context, which is used to parse text values in the script.
     *
     * @param operator the operator to concatenate
     * @return         this context
     */
    @NotNull
    @Contract("_ -> this")
    default ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        throw new IllegalStateException("The current context does not support parser override, use #composed() method instead");
    }

    /**
     * Create a literal replacement in the context, which is used to replace text values in the script.
     *
     * @param str   the string to be replaced
     * @param value the value to replace the string with
     * @return      this context
     */
    @NotNull
    @Contract("_, _ -> this")
    default ComposedContext replace(@NotNull String str, @NotNull Object value) {
        throw new IllegalStateException("The current context does not support literal replacement, use #composed() method instead");
    }

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
