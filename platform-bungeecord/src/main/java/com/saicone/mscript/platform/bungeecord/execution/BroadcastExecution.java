package com.saicone.mscript.platform.bungeecord.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bungeecord.util.Audiences;
import com.saicone.mscript.util.Mini;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Broadcast execution implementation.
 *
 * @author Rubenicos
 */
public class BroadcastExecution extends SingleSection.TextList implements Execution {

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
    public @NotNull Result run(@NotNull Context context) {
        final CommandSender sender = context.get();
        if (sender instanceof ProxiedPlayer && Mini.get().isUsingMiniPlaceholders()) {
            final List<String> lines = getValue(context);
            final Audience main = context.pointer();
            for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                final Audience secondary = Audiences.player(player);
                final Audience relational = Mini.get().relational(main, secondary);
                for (String line : lines) {
                    secondary.sendMessage(Mini.get().parse(relational, line));
                }
            }
            for (String line : getValue(context)) {
                Audiences.console().sendMessage(Mini.get().parse(main, line));
            }
        } else {
            final Audience pointer = context.pointer();
            for (String line : getValue(context)) {
                Audiences.all().sendMessage(Mini.get().parse(pointer, line));
            }
        }

        return Result.DONE;
    }
}
