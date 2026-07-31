package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.Value;
import com.saicone.mscript.io.SectionReader;
import com.saicone.types.TypeParser;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DelayExecution implements Execution {

    public static final TypeParser<Long> PARSER = object -> {
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
    };

    public static final SectionReader<DelayExecution> READER = new SectionReader<>("delay|wait") {
        @Override
        protected DelayExecution read(@NotNull String id, @NotNull Number context) {
            return new DelayExecution(Value.of(PARSER.parse(context)));
        }

        @Override
        protected DelayExecution read(@NotNull String id, @NotNull String context) {
            return new DelayExecution(Value.of(PARSER, context));
        }
    };

    private final Value<Long> delay;

    public DelayExecution(@NotNull Value<Long> delay) {
        this.delay = delay;
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        return Result.delay(delay.get(context), TimeUnit.MILLISECONDS);
    }
}
