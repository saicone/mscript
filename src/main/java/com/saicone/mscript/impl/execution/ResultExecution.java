package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.Value;
import com.saicone.mscript.io.SectionReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResultExecution implements Execution {

    public static final SectionReader<ResultExecution> READER = new SectionReader<>("done|true|continue|break|return|stop|false") {
        @Override
        protected ResultExecution read(@NotNull String id) {
            final Result result;
            switch (id.trim().toLowerCase()) {
                case "done":
                case "true":
                    result = Result.DONE;
                    break;
                case "continue":
                    result = Result.CONTINUE;
                    break;
                case "break":
                    result = Result.BREAK;
                    break;
                case "return":
                case "stop":
                case "false":
                    result = Result.RETURN;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown result: " + id);
            }
            return new ResultExecution(Value.of(result));
        }

        @Override
        public ResultExecution read(@NotNull String id, @Nullable Object context) {
            if (id.equalsIgnoreCase("return")) {
                final Value<Result> value;
                if ("true".equals(context) || "false".equals(context)) {
                    value = Value.of(Result.value(Boolean.parseBoolean((String) context)));
                } else if (context instanceof String str) {
                    value = Value.of(Result::value, str);
                } else {
                    value = Value.of(Result.value(context));
                }

                return new ResultExecution(value);
            }
            return read(id);
        }
    };

    private final Value<Result> value;

    public ResultExecution(@NotNull Value<Result> value) {
        this.value = value;
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        return value.get(context);
    }
}
