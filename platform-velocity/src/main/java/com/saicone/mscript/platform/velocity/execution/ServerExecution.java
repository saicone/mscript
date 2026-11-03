package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.VelocityContext;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server execution implementation.
 *
 * @author Rubenicos
 */
public class ServerExecution extends SingleSection.Text implements VelocityExecution {

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
    public @NotNull Result run(@NotNull VelocityContext context) {
        final Player player = context.get();
        final String server = getValue(context);

        player.createConnectionRequest(context.proxy().getServer(server).get());

        return Result.DONE;
    }
}
