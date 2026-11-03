package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.VelocityContext;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Player command execution implementation.
 *
 * @author Rubenicos
 */
public class PlayerCommandExecution extends SingleSection.Text implements VelocityExecution {

    /**
     * Player command execution reader.
     */
    public static final SectionReader<PlayerCommandExecution> READER = reader("player(-?command)?", PlayerCommandExecution::new);

    /**
     * Constructs a new player command execution.
     *
     * @param object a string representing the command to execute
     */
    public PlayerCommandExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull VelocityContext context) {
        final Player player = context.get();
        final String command = getValue(context);

        context.proxy().getCommandManager().executeAsync(player, command);

        return Result.DONE;
    }

    @Override
    public @NotNull CompletableFuture<Result> runAsync(@NotNull VelocityContext context) {
        final Player player = context.get();
        final String command = getValue(context);

        return context.proxy().getCommandManager()
                .executeAsync(player, command)
                .thenApply(result -> Result.DONE);
    }
}
