package com.saicone.mscript;

import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface Operator {

    Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b);

    enum Arithmetic implements Operator {
        ADD {
            @Override
            public Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                final Object aObject = a.get(context);
                final Object bObject = b.get(context);
                if (aObject != null && bObject != null) {
                    if (aObject instanceof String && bObject instanceof String) {
                        return aObject.toString() + bObject.toString();
                    }
                    return eval0(aObject, bObject);
                }
                throw new UnsupportedOperationException("Cannot eval non-numeric values: " + aObject + " and " + bObject);
            }

            @Override
            public @NotNull Number eval(double a, double b) {
                return a + b;
            }
        },
        SUBTRACT {
            @Override
            public @NotNull Number eval(double a, double b) {
                return a - b;
            }
        },
        MULTIPLY {
            @Override
            public @NotNull Number eval(double a, double b) {
                return a * b;
            }
        },
        DIVIDE {
            @Override
            public @NotNull Number eval(double a, double b) {
                return a / b;
            }
        },
        REMAIN {;
            @Override
            public @NotNull Number eval(double a, double b) {
                return a % b;
            }
        };

        @Override
        public Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            final Object aObject = a.get(context);
            final Object bObject = b.get(context);
            if (aObject != null && bObject != null) {
                return eval0(aObject, bObject);
            }
            throw new UnsupportedOperationException("Cannot eval non-numeric values: " + aObject + " and " + bObject);
        }

        protected Object eval0(@NotNull Object aObject, @NotNull Object bObject) {
            final Double aNumber = Types.DOUBLE.parse(aObject);
            final Double bNumber = Types.DOUBLE.parse(bObject);
            if (aNumber != null && bNumber != null) {
                final Number result = eval(aNumber, bNumber);
                if (aObject instanceof Number) {
                    return Types.of(aObject.getClass()).parse(result);
                } else if (bObject instanceof Number) {
                    return Types.of(bObject.getClass()).parse(result);
                } else {
                    return result;
                }
            }
            throw new UnsupportedOperationException("Cannot eval non-numeric values: " + aObject + " and " + bObject);
        }

        @NotNull
        public Number eval(double a, double b) {
            throw new UnsupportedOperationException("Arithmetic operator " + name() + " does not support direct double evaluation");
        }
    }

    enum Relational implements Operator {
        EQUALS {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                final Object aObject = a.get(context);
                final Object bObject = b.get(context);
                return Objects.equals(aObject, bObject);
            }
        },
        NOT_EQUALS {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                final Object aObject = a.get(context);
                final Object bObject = b.get(context);
                return !Objects.equals(aObject, bObject);
            }
        },
        GREATER_THAN {
            @Override
            public @NotNull Boolean eval(double a, double b) {
                return a > b;
            }
        },
        GREATER_OR_EQUALS {
            @Override
            public @NotNull Boolean eval(double a, double b) {
                return a >= b;
            }
        },
        LESS_THAN {
            @Override
            public @NotNull Boolean eval(double a, double b) {
                return a < b;
            }
        },
        LESS_OR_EQUALS {
            @Override
            public @NotNull Boolean eval(double a, double b) {
                return a <= b;
            }
        };

        @Override
        public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            final Object aObject = a.get(context);
            final Object bObject = b.get(context);
            if (aObject != null && bObject != null) {
                final Double aNumber = Types.DOUBLE.parse(aObject);
                final Double bNumber = Types.DOUBLE.parse(bObject);
                if (aNumber != null && bNumber != null) {
                    return eval(aNumber, bNumber);
                }
            }
            throw new UnsupportedOperationException("Cannot eval non-numeric values: " + aObject + " and " + bObject);
        }

        @NotNull
        public Boolean eval(double a, double b) {
            throw new UnsupportedOperationException("Relational operator " + name() + " does not support direct double evaluation");
        }
    }

    enum Logical implements Operator {
        AND {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                Object aVal = a.get(context);
                if (aVal instanceof Boolean && !(Boolean) aVal) {
                    return false;
                }
                Object bVal = b.get(context);
                if (bVal instanceof Boolean && !(Boolean) bVal) {
                    return false;
                }
                return true;
            }
        },
        OR {
            @Override
            public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
                Object aVal = a.get(context);
                if (aVal instanceof Boolean && (Boolean) aVal) {
                    return true;
                }
                Object bVal = b.get(context);
                if (bVal instanceof Boolean && (Boolean) bVal) {
                    return true;
                }
                return false;
            }
        },
        NOT;

        @Override
        public Boolean eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            throw new UnsupportedOperationException("Logical operator " + name() + " does not support eval");
        }
    }

    enum Bitwise implements Operator {
        AND {
            @Override
            public @NotNull Object eval(long a, long b) {
                return a & b;
            }
        },
        OR {
            @Override
            public @NotNull Object eval(long a, long b) {
                return a | b;
            }
        },
        XOR {
            @Override
            public @NotNull Object eval(long a, long b) {
                return a ^ b;
            }
        };

        @Override
        public Object eval(@NotNull Context context, @NotNull Value<?> a, @NotNull Value<?> b) {
            final Object aObject = a.get(context);
            final Object bObject = b.get(context);
            if (aObject != null && bObject != null) {
                final Long aLong = Types.LONG.parse(aObject);
                final Long bLong = Types.LONG.parse(bObject);
                if (aLong != null && bLong != null) {
                    return eval(aLong, bLong);
                }
            }
            throw new UnsupportedOperationException("Cannot eval non-integer values: " + aObject + " and " + bObject);
        }

        @NotNull
        public Object eval(long a, long b) {
            throw new UnsupportedOperationException("Bitwise operator " + name() + " does not support direct long evaluation");
        }
    }
}
