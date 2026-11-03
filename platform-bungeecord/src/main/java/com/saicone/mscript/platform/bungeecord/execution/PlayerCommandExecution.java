package com.saicone.mscript.platform.bungeecord.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player command execution implementation.
 *
 * @author Rubenicos
 */
public class PlayerCommandExecution extends SingleSection.Text implements Execution {

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
    public @NotNull Result run(@NotNull Context context) {
        final ProxiedPlayer player = context.get();
        final String command = getValue(context);

        ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command);

        return Result.DONE;
    }
}
