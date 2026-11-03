package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.platform.velocity.VelocityContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@ApiStatus.Internal
public interface VelocityExecution extends Execution {

    @Override
    default @NotNull Result run(@NotNull Context context) {
        return run(context.as(VelocityContext.class));
    }

    @Override
    default @NotNull CompletableFuture<Result> runAsync(@NotNull Context context) {
        return runAsync(context.as(VelocityContext.class));
    }

    @NotNull Result run(@NotNull VelocityContext context);

    @NotNull
    default CompletableFuture<Result> runAsync(@NotNull VelocityContext context) {
        return Execution.super.runAsync(context);
    }
}
