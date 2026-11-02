package com.saicone.mscript;

import com.saicone.mscript.util.Values;
import com.saicone.types.Types;
import com.saicone.types.parser.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Represents an operator in the script.<br>
 * In terms of programming, an operator is a symbol that represents an action or operation to be performed on one or more values.<br>
 * In this case the operator take two values and a script context to evaluate the result of the operation.
 *
 * @author Rubenicos
 */
public interface Operator {

    /**
     * Evaluates the operator with the given values and script context.
     *
     * @param context the script context
     * @param a       the first value, aka the left operand
     * @param b       the second value, aka the right operand
     * @return        the result of the operation
     */
    Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b);

    /**
     * Implements operator as arithmetic operation.
     */
    enum Arithmetic implements Operator {
        /**
         * Represents the addition operator (+).<br>
         * It can be used to add two numbers or concatenate two strings.
         */
        ADD("+") {
            @Override
            protected @NotNull Object eval1(@NotNull Object aObject, @NotNull Object bObject) {
                return aObject.toString() + bObject.toString();
            }

            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.add(b);
            }
        },
        /**
         * Represents the subtraction operator (-).<br>
         * It can be used to subtract two numbers.
         */
        SUBTRACT("-") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.subtract(b);
            }
        },
        /**
         * Represents the multiplication operator (*).<br>
         * It can be used to multiply two numbers.
         */
        MULTIPLY("*") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.multiply(b);
            }
        },
        /**
         * Represents the division operator (/).<br>
         * It can be used to divide two numbers.
         */
        DIVIDE("/") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b, @NotNull RoundingMode mode) {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero: " + a + " / 0");
                }
                return a.divide(b, mode);
            }
        },
        /**
         * Represents the remainder operator (%).<br>
         * It can be used to get the remainder of a division between two numbers.
         */
        REMAIN("%") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.remainder(b);
            }
        };

        private final String string;

        Arithmetic(@NotNull String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        @Override
        public Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            final Object aObject = a.get(context);
            final Object bObject = b.get(context);
            if (Values.areUnknown(aObject, bObject)) {
                return null;
            }

            return eval0(aObject, bObject);
        }

        @NotNull
        protected Object eval0(@NotNull Object aObject, @NotNull Object bObject) {
            final BigDecimal aNumber = Types.BIG_DECIMAL.parseOrDefault(aObject, null);
            final BigDecimal bNumber = Types.BIG_DECIMAL.parseOrDefault(bObject, null);
            if (aNumber == null || bNumber == null) {
                return eval1(aObject, bObject);
            }

            final BigDecimal result = eval(aNumber, bNumber).stripTrailingZeros();
            // Try to convert the result back to original type if applicable
            // It is a bit expensive to do, but ensure consistency with non-overflowed and precise operations
            if (result.scale() > 0) {
                return decimalResult(aObject, bObject, result);
            } else {
                return integerResult(aObject, bObject, result);
            }
        }

        @NotNull
        protected Object eval1(@NotNull Object aObject, @NotNull Object bObject) {
            throw new UnsupportedOperationException("Cannot eval non-numeric value on operation: '" + aObject + " " + this + " " + bObject + "'");
        }

        /**
         * Evaluates the operator with the given numeric values using default rounding mode {@link RoundingMode#DOWN}.
         *
         * @param a the first value, aka the left operand
         * @param b the second value, aka the right operand
         * @return  the result of the operation
         */
        @NotNull
        public BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
            return eval(a, b, RoundingMode.DOWN);
        }

        /**
         * Evaluates the operator with the given numeric values using the given rounding mode.
         *
         * @param a    the first value, aka the left operand
         * @param b    the second value, aka the right operand
         * @param mode the rounding mode to be used
         * @return     the result of the operation
         */
        @NotNull
        public BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b, @NotNull RoundingMode mode) {
            return eval(a, b);
        }

        @NotNull
        private Object decimalResult(@NotNull Object aObject, @NotNull Object bObject, @NotNull BigDecimal result) {
            if (aObject instanceof BigDecimal || bObject instanceof BigDecimal) {
                return result;
            } else if (aObject instanceof Double || bObject instanceof Double) {
                if (NumberParser.DOUBLE.isInRange(result)) {
                    return result.doubleValue();
                }
            } else if (aObject instanceof Float || bObject instanceof Float) {
                if (NumberParser.FLOAT.isInRange(result)) {
                    return result.floatValue();
                }
            }

            // Avoid big number if possible
            if (NumberParser.DOUBLE.isInRange(result)) {
                return result.doubleValue();
            } else {
                return result;
            }
        }

        @NotNull
        private Object integerResult(@NotNull Object aObject, @NotNull Object bObject, @NotNull BigDecimal result) {
            if (aObject instanceof BigInteger || bObject instanceof BigInteger) {
                return result.toBigInteger();
            } else if (aObject instanceof Long || bObject instanceof Long) {
                if (NumberParser.LONG.isInRange(result)) {
                    return result.longValue();
                }
            } else if (aObject instanceof Integer || bObject instanceof Integer) {
                if (NumberParser.INTEGER.isInRange(result)) {
                    return result.intValue();
                }
            } else if (aObject instanceof Short || bObject instanceof Short) {
                if (NumberParser.SHORT.isInRange(result)) {
                    return result.shortValue();
                }
            } else if (aObject instanceof Byte || bObject instanceof Byte) {
                if (NumberParser.BYTE.isInRange(result)) {
                    return result.byteValue();
                }
            }

            // Avoid big number if possible
            if (NumberParser.LONG.isInRange(result)) {
                return result.longValue();
            } else {
                return result.toBigInteger();
            }
        }
    }

    /**
     * Implements operator as relational operation.
     */
    enum Relational implements Operator {
        /**
         * Represents the equals operator (==).<br>
         * It can be used to compare two values for equality.
         */
        EQUALS("==") {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                final Object aObject = a.get(context);
                final Object bObject = b.get(context);
                return Values.equals(aObject, bObject);
            }

            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) == 0;
            }
        },
        /**
         * Represents the not equals operator (!=).<br>
         * It can be used to compare two values for inequality.
         */
        NOT_EQUALS("!=") {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                final Object aObject = a.get(context);
                final Object bObject = b.get(context);
                return Values.notEquals(aObject, bObject);
            }

            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) != 0;
            }
        },
        /**
         * Represents the greater than operator (>).<br>
         * It can be used to compare two values for greater than.
         */
        GREATER_THAN(">") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) > 0;
            }
        },
        /**
         * Represents the greater than or equals operator (>=).<br>
         * It can be used to compare two values for greater than or equals.
         */
        GREATER_OR_EQUALS(">=") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) >= 0;
            }
        },
        /**
         * Represents the less than operator (<).<br>
         * It can be used to compare two values for less than.
         */
        LESS_THAN("<") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) < 0;
            }
        },
        /**
         * Represents the less than or equals operator (<=).<br>
         * It can be used to compare two values for less than or equals.
         */
        LESS_OR_EQUALS("<=") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) <= 0;
            }
        };

        private final String string;

        Relational(@NotNull String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        @Override
        public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            final Object aObject = a.get(context);
            final Object bObject = b.get(context);
            if (Values.areUnknown(aObject, bObject)) {
                return null;
            }

            final BigDecimal aNumber = Types.BIG_DECIMAL.parseOrDefault(aObject, null);
            final BigDecimal bNumber = Types.BIG_DECIMAL.parseOrDefault(bObject, null);
            if (aNumber != null && bNumber != null) {
                return eval(aNumber, bNumber);
            }

            throw new UnsupportedOperationException("Cannot eval non-numeric value on operation: '" + aObject + " " + this + " " + bObject + "'");
        }

        /**
         * Evaluates the operator with the given numeric values.
         *
         * @param a the first value, aka the left operand
         * @param b the second value, aka the right operand
         * @return  the result of the operation
         */
        public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
            throw new IllegalStateException();
        }
    }

    /**
     * Implements operator as logical operation that handle the term of "tristate"<br>
     * If a value is {@code unknown}, {@code null} or {@code undefined} it will be ignored
     */
    enum Logical implements Operator {
        /**
         * Represents the logical AND operator (&&).<br>
         * It can be used to combine two boolean values.
         */
        AND("&&") {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                Object aObject = a.get(context);
                if (Values.isValid(aObject) && Values.isFalse(aObject)) {
                    return false;
                }
                Object bObject = b.get(context);
                if (Values.isValid(bObject) && Values.isFalse(bObject)) {
                    return false;
                }
                return Values.isUnknown(aObject) && Values.isUnknown(bObject) ? null : true;
            }
        },
        /**
         * Represents the logical OR operator (||).<br>
         * It can be used to combine two boolean values.
         */
        OR("||") {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                Object aObject = a.get(context);
                if (Values.isValid(aObject) && Values.isTrue(aObject)) {
                    return true;
                }
                Object bObject = b.get(context);
                if (Values.isValid(bObject) && Values.isTrue(bObject)) {
                    return true;
                }
                return Values.isUnknown(aObject) && Values.isUnknown(bObject) ? null : false;
            }
        };

        private final String string;

        Logical(@NotNull String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        @Override
        public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            throw new UnsupportedOperationException("Logical operator " + name() + " does not support eval");
        }
    }

    /**
     * Implements operator as bitwise operation.
     */
    enum Bitwise implements Operator {
        /**
         * Represents the bitwise AND operator (&).<br>
         * It can be used to perform a bitwise AND operation on two integer or boolean values.
         */
        AND("&") {
            @Override
            public long eval(long a, long b) {
                return a & b;
            }
        },
        /**
         * Represents the bitwise OR operator (|).<br>
         * It can be used to perform a bitwise OR operation on two integer or boolean values.
         */
        OR("|") {
            @Override
            public long eval(long a, long b) {
                return a | b;
            }
        },
        /**
         * Represents the bitwise XOR operator (^).<br>
         * It can be used to perform a bitwise XOR operation on two integer or boolean values.
         */
        XOR("^") {
            @Override
            public long eval(long a, long b) {
                return a ^ b;
            }
        };

        private final String string;

        Bitwise(@NotNull String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }

        @Override
        public Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            Object aObject = a.get(context);
            Object bObject = b.get(context);
            if (Values.areUnknown(aObject, bObject)) {
                return null;
            }

            // Convert any boolean String representation
            aObject = booleanOrObject(aObject);
            bObject = booleanOrObject(bObject);

            final Long aLong = Types.LONG.parseOrDefault(aObject, null);
            final Long bLong = Types.LONG.parseOrDefault(bObject, null);
            if (aLong != null && bLong != null) {
                final long result = eval(aLong, bLong);
                // Try to convert the result back to original type if applicable
                if (aObject instanceof Boolean && bObject instanceof Boolean) {
                    return result != 0;
                }
                return result;
            }

            throw new UnsupportedOperationException("Cannot eval non-integer value on operation: '" + aObject + " " + this + " " + bObject + "'");
        }

        /**
         * Evaluates the operator with the given integer values.
         *
         * @param a the first value, aka the left operand
         * @param b the second value, aka the right operand
         * @return  the result of the operation
         */
        public long eval(long a, long b) {
            throw new IllegalStateException();
        }

        @NotNull
        private Object booleanOrObject(@NotNull Object object) {
            if (object instanceof String) {
                final Boolean bool = Types.BOOLEAN.parseOrDefault(object, null);
                if (bool != null) {
                    return bool;
                }
            }
            return object;
        }
    }
}
