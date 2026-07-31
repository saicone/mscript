package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class Result {

    public static final Result UNKNOWN = new Result() {
        @Override
        public boolean isUnknown() {
            return true;
        }
    };
    public static final Result DONE = new Result() {
        @Override
        public boolean isDone() {
            return true;
        }
    };

    public static final Result CONTINUE = new Result() {
        @Override
        public boolean isContinue() {
            return true;
        }
    };
    public static final Result BREAK = new Result() {
        @Override
        public boolean isBreak() {
            return true;
        }
    };
    public static final Result RETURN = new Result() {
        @Override
        public boolean isReturn() {
            return true;
        }
    };

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

    public boolean isUnknown() {
        return false;
    }

    public boolean isDone() {
        return false;
    }

    public boolean isContinue() {
        return false;
    }

    public boolean isBreak() {
        return false;
    }

    public boolean isReturn() {
        return false;
    }

    public boolean isDelayed() {
        return time > 0;
    }

    @Nullable
    public Object value() {
        return value;
    }

    public long time() {
        return time;
    }

    @NotNull
    public TimeUnit unit() {
        return unit;
    }
}
