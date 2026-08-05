package com.saicone.mscript;

import com.saicone.mscript.util.Values;
import com.saicone.types.Types;
import com.saicone.types.parser.NumberParser;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public interface Operator {

    Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b);

    enum Arithmetic implements Operator {
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
        SUBTRACT("-") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.subtract(b);
            }
        },
        MULTIPLY("*") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.multiply(b);
            }
        },
        DIVIDE("/") {
            @Override
            public @NotNull BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b, @NotNull RoundingMode mode) {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero: " + a + " / 0");
                }
                return a.divide(b, mode);
            }
        },
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

        @NotNull
        public BigDecimal eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
            return eval(a, b, RoundingMode.DOWN);
        }

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

    enum Relational implements Operator {
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
        GREATER_THAN(">") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) > 0;
            }
        },
        GREATER_OR_EQUALS(">=") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) >= 0;
            }
        },
        LESS_THAN("<") {
            @Override
            public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
                return a.compareTo(b) < 0;
            }
        },
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

        public boolean eval(@NotNull BigDecimal a, @NotNull BigDecimal b) {
            throw new IllegalStateException();
        }
    }

    /**
     * Logical operator that handle the term of "tristate"<br>
     * If a value is unknown (null or undefined) it will be ignored
     */
    enum Logical implements Operator {
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

    enum Bitwise implements Operator {
        AND("&") {
            @Override
            public long eval(long a, long b) {
                return a & b;
            }
        },
        OR("|") {
            @Override
            public long eval(long a, long b) {
                return a | b;
            }
        },
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
