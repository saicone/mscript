package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DelayExecution extends SingleSection<Long> implements Execution {

    public static final SectionReader<DelayExecution> READER = reader("delay|wait", DelayExecution::new);

    public DelayExecution(@Nullable Object value) {
        super(value);
    }

    @Override
    protected @NotNull Long parse(@NotNull Object object) {
        if (object instanceof Number number) {
            return number.longValue();
        } else if (object instanceof String str) {
            final Duration duration = Types.DURATION.parse(str);
            if (duration == null) {
                throw new IllegalArgumentException("Invalid duration: " + str);
            }
            return duration.toMillis();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        }
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        return Result.delay(getValue(context), TimeUnit.MILLISECONDS);
    }
}
