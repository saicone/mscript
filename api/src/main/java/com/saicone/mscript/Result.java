package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Represents the result of a script execution.<br>
 * In terms of programming, a result is the outcome of program execution.
 *
 * @author Rubenicos
 */
public class Result {

    /**
     * Represents an unknown result, which means that the result is not yet known or determined.
     */
    public static final Result UNKNOWN = new Result() {
        @Override
        public boolean isUnknown() {
            return true;
        }
    };
    /**
     * Represents a result that indicates the script execution is done,
     * which means that the script has completed its execution and there are no further actions to be taken.<br>
     * This is the equivalent in programming to result {@code 0}.
     */
    public static final Result DONE = new Result() {
        @Override
        public boolean isDone() {
            return true;
        }
    };

    /**
     * Represents a result that indicates the script execution should continue,
     * which means that the script has not yet completed its execution and can continue the current loop.<br>
     * This is the equivalent in programming to {@code continue;}.
     */
    public static final Result CONTINUE = new Result() {
        @Override
        public boolean isContinue() {
            return true;
        }
    };
    /**
     * Represents a result that indicates the script execution should break,
     * which means that the script has not yet completed its execution and should exit the current loop.<br>
     * This is the equivalent in programming to {@code break;}.
     */
    public static final Result BREAK = new Result() {
        @Override
        public boolean isBreak() {
            return true;
        }
    };
    /**
     * Represents a result that indicates the script execution should return,
     * which means that the script has not yet completed its execution and should exit the current method or function.<br>
     * This is the equivalent in programming to {@code return;}.
     */
    public static final Result RETURN = new Result() {
        @Override
        public boolean isReturn() {
            return true;
        }
    };

    /**
     * Creates a result that indicates the script execution should return with the given value,
     * which means that the script has not yet completed its execution and should exit the current method or function.<br>
     * This is the equivalent in programming to {@code return value;}.
     *
     * @param value the value to be returned
     * @return      a result that indicates the script execution should return with the given value
     */
    @NotNull
    public static Result value(@Nullable Object value) {
        if (value == null) {
            return RETURN;
        }

        final Result result = new Result() {
            @Override
            public boolean isReturn() {
                return true;
            }
        };
        result.value = value;
        return result;
    }

    /**
     * Creates a result that indicates the script execution is delayed for the given time and unit,
     * which means that the script execution is postponed for a certain amount of time.<br>
     * This is the equivalent in programming to {@code Thread.sleep(milliseconds)}.
     *
     * @param time the time to delay
     * @param unit the unit of time to delay
     * @return     a result that indicates the script execution is delayed for the given time and unit
     */
    @NotNull
    public static Result delay(long time, @NotNull TimeUnit unit) {
        if (time <= 0) {
            throw new IllegalArgumentException("Delay time must be greater than 0");
        }

        final Result result = new Result();
        result.time = time;
        result.unit = unit;
        return result;
    }

    protected Object value;
    protected long time;
    protected TimeUnit unit;

    /**
     * Checks if the result is unknown, which means that the result is not yet known or determined.
     *
     * @return true if the result is unknown, false otherwise
     */
    public boolean isUnknown() {
        return false;
    }

    /**
     * Checks if the result indicates that the script execution is done,
     * which means that the script has completed its execution and there are no further actions to be taken.
     *
     * @return true if the result indicates that the script execution is done, false otherwise
     */
    public boolean isDone() {
        return false;
    }

    /**
     * Checks if the result indicates that the script execution should continue,
     * which means that the script has not yet completed its execution and can continue the current loop.
     *
     * @return true if the result indicates that the script execution should continue, false otherwise
     */
    public boolean isContinue() {
        return false;
    }

    /**
     * Checks if the result indicates that the script execution should break,
     * which means that the script has not yet completed its execution and should exit the current loop.
     *
     * @return true if the result indicates that the script execution should break, false otherwise
     */
    public boolean isBreak() {
        return false;
    }

    /**
     * Checks if the result indicates that the script execution should return,
     * which means that the script has not yet completed its execution and should exit the current method or function.
     *
     * @return true if the result indicates that the script execution should return, false otherwise
     */
    public boolean isReturn() {
        return false;
    }

    /**
     * Checks if the result indicates that the script execution is delayed,
     * which means that the script execution is postponed for a certain amount of time.
     *
     * @return true if the result indicates that the script execution is delayed, false otherwise
     */
    public boolean isDelayed() {
        return time > 0;
    }

    /**
     * Returns the value of the result.
     *
     * @return the value of the result, or null if the result has no value
     */
    @Nullable
    public Object value() {
        return value;
    }

    /**
     * Returns the delay time of the result.
     *
     * @return the delay time of the result, or 0 if the result has no delay
     */
    public long time() {
        return time;
    }

    /**
     * Returns the time unit of the result.
     *
     * @return the time unit of the result, or null if the result has no time unit
     */
    @NotNull
    public TimeUnit unit() {
        return unit;
    }
}
