package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.VelocityContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Console command execution implementation.
 *
 * @author Rubenicos
 */
public class ConsoleCommandExecution extends SingleSection.Text implements VelocityExecution {

    /**
     * Console command execution reader.
     */
    public static final SectionReader<ConsoleCommandExecution> READER = reader("console(-?command)?", ConsoleCommandExecution::new);

    /**
     * Constructs a new console command execution.
     *
     * @param object a string representing the command to execute
     */
    public ConsoleCommandExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull VelocityContext context) {
        final String command = getValue(context);

        context.proxy().getCommandManager().executeAsync(context.proxy().getConsoleCommandSource(), command);

        return Result.DONE;
    }

    @Override
    public @NotNull CompletableFuture<Result> runAsync(@NotNull VelocityContext context) {
        final String command = getValue(context);

        return context.proxy().getCommandManager()
                .executeAsync(context.proxy().getConsoleCommandSource(), command)
                .thenApply(result -> Result.DONE);
    }
}
