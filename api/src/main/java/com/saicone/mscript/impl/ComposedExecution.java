package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * An execution implementation that represent a regular if-else statement, with a condition and two execution lists.
 *
 * @author Rubenicos
 */
public class ComposedExecution implements Execution {

    private static final Iterator<Execution> EMPTY_ITERATOR = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Execution next() {
            throw new UnsupportedOperationException("No elements in iterator");
        }
    };

    private final Condition condition;
    private final List<Execution> ifExecution;
    private final List<Execution> elseExecution;

    /**
     * Constructs a new composed execution with the given condition and execution lists.
     *
     * @param condition     the condition to evaluate, can be null (always true)
     * @param ifExecution   the list of executions to run if the condition is true
     * @param elseExecution the list of executions to run if the condition is false
     */
    public ComposedExecution(@Nullable Condition condition, @NotNull List<Execution> ifExecution, @NotNull List<Execution> elseExecution) {
        this.condition = condition;
        this.ifExecution = ifExecution;
        this.elseExecution = elseExecution;
    }

    /**
     * Get the condition of this composed execution.
     *
     * @return the condition, or null if always true
     */
    @Nullable
    public Condition condition() {
        return condition;
    }

    /**
     * Get the list of executions to run if the condition is true.
     *
     * @return the list of executions
     */
    @NotNull
    public List<Execution> ifExecution() {
        return ifExecution;
    }

    /**
     * Get the list of executions to run if the condition is false.
     *
     * @return the list of executions
     */
    @NotNull
    public List<Execution> elseExecution() {
        return elseExecution;
    }

    /**
     * Evaluates the condition and returns the list of executions to run based on the result.
     *
     * @param context the context to evaluate the condition
     * @return        the list of executions to run, or null if the condition is neither true nor false
     */
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

    /**
     * Returns an iterator over the executions to run based on the evaluation of the condition.
     *
     * @param context the context to evaluate the condition
     * @return        an iterator over the executions to run
     */
    @NotNull
    public Iterator<Execution> iterator(@NotNull Context context) {
        final List<Execution> run = execution(context);
        if (run == null || run.isEmpty()) {
            return EMPTY_ITERATOR;
        }

        return new Iterator<>() {
            final Iterator<Execution> main = run.iterator();
            Iterator<Execution> sub;

            @Override
            public boolean hasNext() {
                if (sub != null) {
                    if (sub.hasNext()) {
                        return true;
                    } else {
                        sub = null;
                    }
                }
                return main.hasNext();
            }

            @Override
            public Execution next() {
                if (sub != null) {
                    return sub.next();
                } else {
                    final Execution execution = main.next();
                    if (execution instanceof ComposedExecution composed) {
                        sub = composed.iterator(context);
                        return next();
                    } else {
                        return execution;
                    }
                }
            }
        };
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        return runNow0(context, iterator(context));
    }

    @NotNull
    protected Result runNow0(@NotNull Context context, @NotNull Iterator<Execution> iterator) {
        Result result = Result.UNKNOWN;

        while (iterator.hasNext()) {
            final Execution execution = iterator.next();
            final Result current = execution.run(context);
            if (current.isDone()) {
                result = current;
            } else if (current.isContinue()) {
                continue;
            } else if (current.isBreak()) {
                break;
            } else if (current.isReturn()) {
                return current;
            } else if (current.isDelayed()) {
                result = current;
                // Go outside the scope, since a synchronous execution cannot be delayed
                context.delay(current.time(), current.unit(), () -> {
                    runNow0(context, iterator);
                });
                break;
            }
        }

        return result;
    }

    @Override
    public @NotNull CompletableFuture<Result> runAsync(@NotNull Context context) {
        final CompletableFuture<Result> future = new CompletableFuture<>();
        context.async(() -> {
            runAsync0(context, iterator(context), future);
        });
        return future;
    }

    protected void runAsync0(@NotNull Context context, @NotNull Iterator<Execution> iterator, @NotNull CompletableFuture<Result> future) {
        try {
            runAsync1(context, iterator, future);
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
    }

    protected void runAsync1(@NotNull Context context, @NotNull Iterator<Execution> iterator, @NotNull CompletableFuture<Result> future) {
        Result result = Result.UNKNOWN;

        while (iterator.hasNext()) {
            final Execution execution = iterator.next();
            final Result current = execution.run(context);
            if (current.isDone()) {
                result = current;
            } else if (current.isContinue()) {
                continue;
            } else if (current.isBreak()) {
                break;
            } else if (current.isReturn()) {
                future.complete(current);
                return;
            } else if (current.isDelayed()) {
                // This assignation doesn't really do anything, but I left it as remainder about how the execution works
                result = current;
                // Finish the execution outside the scope after delay
                context.delayAsync(current.time(), current.unit(), () -> {
                    runAsync0(context, iterator, future);
                });
                return;
            }
        }

        future.complete(result);
    }
}
