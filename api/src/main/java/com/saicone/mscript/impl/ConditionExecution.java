package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * An execution implementation that represent a regular if-else statement, with a condition and two executions.
 *
 * @author Rubenicos
 */
public class ConditionExecution implements Execution {

    private final Condition condition;
    private final Execution ifExecution;
    private final Execution elseExecution;

    /**
     * Constructs a conditional execution.
     *
     * @param condition      the condition to evaluate
     * @param ifExecution    the execution to run if the condition is true
     * @param elseExecution  the execution to run if the condition is false
     */
    public ConditionExecution(@Nullable Condition condition, @NotNull Execution ifExecution, @NotNull Execution elseExecution) {
        this.condition = condition;
        this.ifExecution = ifExecution;
        this.elseExecution = elseExecution;
    }

    /**
     * Get the condition of this execution.
     *
     * @return the condition, or null if always true
     */
    @Nullable
    public Condition condition() {
        return condition;
    }

    /**
     * Get the execution to run if the condition is true.
     *
     * @return an execution
     */
    @NotNull
    public Execution ifExecution() {
        return ifExecution;
    }

    /**
     * Get the execution to run if the condition is false.
     *
     * @return an execution
     */
    @NotNull
    public Execution elseExecution() {
        return elseExecution;
    }

    /**
     * Evaluates the condition and returns the executon to run based on the result.
     *
     * @param context the context to evaluate the condition
     * @return        the execution to run, or null if the condition is neither true nor false
     */
    @Nullable
    public Execution execution(@NotNull Context context) {
        final Condition condition = condition();
        final Boolean result = condition != null ? condition.test(context) : Boolean.TRUE;

        if (Boolean.TRUE.equals(result)) {
            return ifExecution();
        } else if (Boolean.FALSE.equals(result)) {
            return elseExecution();
        } else {
            return null;
        }
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final Execution execution = execution(context);
        if (execution == null) {
            return Result.UNKNOWN;
        }

        return execution.run(context);
    }

    @Override
    public @NotNull CompletableFuture<Result> runAsync(@NotNull Context context) {
        final Execution execution = execution(context);
        if (execution == null) {
            return CompletableFuture.completedFuture(Result.UNKNOWN);
        }
        return execution.runAsync(context);
    }
}
