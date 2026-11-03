package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.VelocityContext;
import com.saicone.mscript.util.Mini;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Broadcast execution implementation.
 *
 * @author Rubenicos
 */
public class BroadcastExecution extends SingleSection.TextList implements VelocityExecution {

    /**
     * Broadcast execution reader.
     */
    public static final SectionReader<BroadcastExecution> READER = reader("(mini-?)?broadcast-?(message|msg)?", BroadcastExecution::new);

    /**
     * Constructs a new broadcast execution.
     *
     * @param object a string or list of strings representing the messages to broadcast
     */
    public BroadcastExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull VelocityContext context) {
        final CommandSource source = context.get();
        if (source instanceof Player && Mini.get().isUsingMiniPlaceholders()) {
            final List<String> lines = getValue(context);
            final Audience main = context.pointer();
            for (Player secondary : context.proxy().getAllPlayers()) {
                final Audience relational = Mini.get().relational(main, secondary);
                for (String line : lines) {
                    secondary.sendMessage(Mini.get().parse(relational, line));
                }
            }
            for (String line : getValue(context)) {
                context.proxy().getConsoleCommandSource().sendMessage(Mini.get().parse(main, line));
            }
        } else {
            final Audience pointer = context.pointer();
            for (String line : getValue(context)) {
                context.proxy().sendMessage(Mini.get().parse(pointer, line));
            }
        }

        return Result.DONE;
    }
}
