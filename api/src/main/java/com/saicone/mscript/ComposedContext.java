package com.saicone.mscript;

import com.saicone.mscript.context.EngineContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

/**
 * A composed context is generally used to delegate bindings and attributes
 * to another script processor in a language that can understand.<br>
 * Or in a simple way, to assign replacements to parse text values.
 *
 * @author Rubenicos
 */
public interface ComposedContext extends EngineContext {

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
}
