package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

/**
 * Represents a value in the script.<br>
 * In terms of programming, a value is a piece of data that can be used in expressions, conditions, and executions.<br>
 * A value implementation can be a literal value, a parseable, a chained parseable, or even a complex expression:<br>
 * <ul>
 * <li><a id="literal"><b>Literal</b></a>
 * <p> Is the value itself, without any modification or parsing.</p>
 * </li>
 * <li><a id="parseable"><b>Parseable</b></a>
 * <p> Is a text value that can be parsed from script context.</p>
 * </li>
 * <li><a id="chained-parseable"><b>Chained Parseable</b></a>
 * <p> Is a parseable value converted into a different type of data.</p>
 * </li>
 * <li><a id="complex-expression"><b>Complex Expression</b></a>
 * <p> Is the implementation itself of the value interface, which a script context
 *     is provided to obtain the value that is required.</p>
 * </li>
 * </ul>
 *
 * @author Rubenicos
 *
 * @param <T> the type of the value
 */
@FunctionalInterface
public interface Value<T> extends Section {

    /**
     * A value that always returns null.
     */
    Value<?> NULL = context -> null;
    /**
     * A value that always returns true.
     */
    Value<?> TRUE = context -> true;
    /**
     * A value that always returns false.
     */
    Value<?> FALSE = context -> false;

    /**
     * Creates a literal value that always returns the given value.
     *
     * @param value the value to be returned
     * @return a    value that always returns the given value
     * @param <T>   the type of the value
     */
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

    /**
     * Creates a parseable value that parses the given string from the script context.
     *
     * @param str the string to be parsed
     * @return    a value that parses the given string from the script context
     */
    @NotNull
    static Value<String> using(@NotNull String str) {
        return context -> context.parse(str);
    }

    /**
     * Creates a chained parseable value that parses the given string from the
     * script context and converts it to a different type of data.
     *
     * @param parser the function to convert the parsed value
     * @param str    the string to be parsed
     * @return a     value that parses the given string from the script context and converts it to a different type of data
     * @param <T>    the type of the value

     */
    @NotNull
    static <T> Value<T> using(@NotNull Function<Object, T> parser, @NotNull String str) {
        return context -> parser.apply(context.parse(str));
    }

    /**
     * Returns the value of this object, using the given script context.
     *
     * @param context the script context to be used
     * @return        the value of this object
     */
    @UnknownNullability
    T get(@NotNull Context context);

    /**
     * Returns a new value that applies the given operator to this value and the given value.
     *
     * @param operator the operator to be applied
     * @param b        the value to be combined with this value
     * @return         a new value that applies the given operator to this value and the given value
     */
    @NotNull
    default Value<Object> andThen(@NotNull Operator operator, @NotNull Value<T> b) {
        return context -> operator.eval(context, this, b);
    }

    /**
     * Returns a new value that applies the given parser to this value.
     *
     * @param parser the parser to be applied
     * @return       a new value that applies the given parser to this value
     * @param <A>    the type of the value after applying the parser
     */
    @NotNull
    default <A> Value<A> andThen(@NotNull Function<T, A> parser) {
        return context -> parser.apply(get(context));
    }
}
