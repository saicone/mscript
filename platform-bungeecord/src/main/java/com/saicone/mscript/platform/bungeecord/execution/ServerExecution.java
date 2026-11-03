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
 * Server execution implementation.
 *
 * @author Rubenicos
 */
public class ServerExecution extends SingleSection.Text implements Execution {

    /**
     * Server execution reader.
     */
    public static final SectionReader<ServerExecution> READER = reader("connect|proxy|bungee|velocity|server", ServerExecution::new);

    /**
     * Constructs a new server execution.
     *
     * @param object a string representing the server name to connect to
     */
    public ServerExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final ProxiedPlayer player = context.get();
        final String server = getValue(context);

        player.connect(ProxyServer.getInstance().getServerInfo(server));

        return Result.DONE;
    }
}
