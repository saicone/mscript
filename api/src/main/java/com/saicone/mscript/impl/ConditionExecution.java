package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ConditionExecution implements Execution {

    private final Condition condition;
    private final List<Execution> ifExecution;
    private final List<Execution> elseExecution;

    public ConditionExecution(@Nullable Condition condition, @NotNull List<Execution> ifExecution, @NotNull List<Execution> elseExecution) {
        this.condition = condition;
        this.ifExecution = ifExecution;
        this.elseExecution = elseExecution;
    }

    @Nullable
    public Condition condition() {
        return condition;
    }

    @Nullable
    public List<Execution> execution(@NotNull Context context) {
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

    @NotNull
    public List<Execution> ifExecution() {
        return ifExecution;
    }

    @NotNull
    public List<Execution> elseExecution() {
        return elseExecution;
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final List<Execution> run = execution(context);
        if (run == null || run.isEmpty()) {
            return Result.UNKNOWN;
        }

        return run(context, run, 0);
    }

    @NotNull
    protected Result run(@NotNull Context context, @NotNull List<Execution> run, int index) {
        Result lastResult = Result.UNKNOWN;
        for (int i = index; i < run.size(); i++) {
            final Execution execution = run.get(i);
            final Result result = execution.run(context);
            if (result.isDone()) {
                lastResult = result;
            } else if (result.isContinue()) {
                continue;
            } else if (result.isBreak()) {
                break;
            } else if (result.isReturn()) {
                return result;
            } else if (result.isDelayed()) {
                // Execute delay outside the scope, since a synchronous execution cannot be delayed
                final int next = i + 1;
                context.delay(result.time(), result.unit(), () -> {
                    run(context, run, next);
                });
                // Tell the caller that the last execution is delayed
                lastResult = result;
                break;
            }
        }

        return lastResult;
    }

    @Override
    public @NotNull CompletableFuture<Result> runAsync(@NotNull Context context) {
        final CompletableFuture<Result> future = new CompletableFuture<>();
        context.async(() -> {
            final List<Execution> run = execution(context);
            if (run == null || run.isEmpty()) {
                future.complete(Result.UNKNOWN);
                return;
            }

            runAsync(context, future, Result.UNKNOWN, run, 0);
        });
        return future;
    }

    protected void runAsync(@NotNull Context context, @NotNull CompletableFuture<Result> future, @NotNull Result lastResult, @NotNull List<Execution> run, int index) {
        try {
            if (index < run.size()) {
                final Execution execution = run.get(index);
                execution.runAsync(context).whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                    } else if (result.isDone()) {
                        runAsync(context, future, result, run, index + 1);
                    } else if (result.isContinue()) {
                        runAsync(context, future, lastResult, run, index + 1);
                    } else if (result.isBreak()) {
                        future.complete(lastResult);
                    } else if (result.isReturn()) {
                        future.complete(result);
                    } else if (result.isDelayed()) {
                        context.delay(result.time(), result.unit(), () -> {
                            runAsync(context, future, lastResult, run, index + 1);
                        });
                    }
                });
            } else {
                future.complete(lastResult);
            }
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }
}
