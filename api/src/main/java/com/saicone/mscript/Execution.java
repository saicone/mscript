package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an execution of a script.<br>
 * In terms of programming, an execution is a code block that can be executed.
 *
 * @author Rubenicos
 */
@FunctionalInterface
public interface Execution extends Section {

    /**
     * Creates an empty execution that does nothing and returns {@link Result#CONTINUE}.
     *
     * @return a newly created empty execution
     */
    @NotNull
    static Execution empty() {
        return context -> Result.CONTINUE;
    }

    /**
     * Runs the execution with the given script context and returns the result of the execution.
     *
     * @param context the context in which the execution is run
     * @return        the result of the execution
     */
    @NotNull
    Result run(@NotNull Context context);

    /**
     * Runs the execution asynchronously with the given script context and returns a {@link CompletableFuture}
     * that will be completed with the result of the execution.<br>
     * In other words, this method return the result of the execution until it is completed after any
     * heavy operation or delayed result.
     *
     * @param context the context in which the execution is run
     * @return        a {@link CompletableFuture} that will be completed with the result of the execution
     */
    @NotNull
    default CompletableFuture<Result> runAsync(@NotNull Context context) {
        final CompletableFuture<Result> future = new CompletableFuture<>();
        context.async(() -> {
            try {
                future.complete(run(context));
            } catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
