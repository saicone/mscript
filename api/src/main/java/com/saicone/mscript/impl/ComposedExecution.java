package com.saicone.mscript.impl;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A composed execution that runs a list of executions sequentially, handling
 * nested composed executions and condition evaluations through its iterator.
 * Delayed results are resumed asynchronously outside the current scope.
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

    private final List<Execution> executions;

    /**
     * Constructs a composed execution.
     *
     * @param executions the executions to run
     */
    public ComposedExecution(@NotNull List<Execution> executions) {
        this.executions = executions;
    }

    /**
     * Get the executions of this composed execution.
     *
     * @return the executions
     */
    @NotNull
    public List<Execution> getExecutions() {
        return executions;
    }

    /**
     * Get an iterator of executions to run.
     *
     * @param context the context to run the executions
     * @return        an iterator of executions
     */
    @NotNull
    public Iterator<Execution> iterator(@NotNull Context context) {
        final List<Execution> executions = getExecutions();
        if (executions.isEmpty()) {
            return EMPTY_ITERATOR;
        }

        return new ComposedIterator(context, executions.iterator());
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

    /**
     * An iterator that flattens nested composed executions and evaluates
     * condition executions lazily as they are encountered.
     */
    public static class ComposedIterator implements Iterator<Execution> {

        private final Context context;
        private final Iterator<Execution> main;

        private Iterator<Execution> sub;

        /**
         * Constructs a composed iterator.
         *
         * @param context the context to run the executions
         * @param main    the main iterator
         */
        public ComposedIterator(@NotNull Context context, @NotNull Iterator<Execution> main) {
            this.context = context;
            this.main = main;
        }

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
                Execution execution = main.next();
                if (execution instanceof ConditionExecution condition) {
                    execution = eval(condition);
                }

                if (execution instanceof ComposedExecution composed) {
                    sub = composed.iterator(context);
                    return next();
                } else {
                    return execution;
                }
            }
        }

        @NotNull
        private Execution eval(@NotNull ConditionExecution execution) {
            final Execution result = execution.execution(context);
            if (result == null) {
                return Execution.empty();
            } else if (result instanceof ConditionExecution condition) {
                return eval(condition);
            } else {
                return execution;
            }
        }
    }
}
