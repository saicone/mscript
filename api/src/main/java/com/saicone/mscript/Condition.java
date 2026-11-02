package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a condition in the script.<br>
 * In terms of programming, a condition is a boolean expression that can be evaluated to true or false.<br>
 * But in this case, a condition result can also be null, which means that the condition is not yet known or determined,
 * so can be ignored in any future evaluation.
 *
 * @author Rubenicos
 */
@FunctionalInterface
public interface Condition extends Section {

    /**
     * Tests the condition with the given script context and returns the result of the condition.<br>
     * In other words, this method returns true if the condition is met, false if it is not met,
     * or null if the condition is not yet known or determined.
     *
     * @param context the context in which the condition is tested
     * @return        a tristate boolean value
     */
    @Nullable
    Boolean test(@NotNull Context context);
}
