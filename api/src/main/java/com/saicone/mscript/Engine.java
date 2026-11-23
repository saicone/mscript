package com.saicone.mscript;

import com.saicone.mscript.context.EngineContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents an additional script processor that take a String input and evaluates it with an output.<br>
 * Normally, this is used to run blocks of code from other languages, for example, JavaScript.<br>
 * And providing bindings and attributes from {@link EngineContext}.
 *
 * @author Rubenicos
 */
public interface Engine extends AutoCloseable {

    /**
     * Evaluates the given script with the given context.<br>
     * Any object can be return, including null objects.
     *
     * @param context the context to use
     * @param script  the script to evaluate
     * @return        an object result of the evaluation
     * @throws Throwable if any error occurs
     */
    @UnknownNullability
    Object eval(@NotNull EngineContext context, @NotNull String script) throws Throwable;
}
