package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface Execution extends Section {

    @NotNull
    static Execution empty() {
        return context -> Result.CONTINUE;
    }

    @NotNull
    Result run(@NotNull Context context);

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
