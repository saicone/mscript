package com.saicone.mscript.platform.velocity.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server condition implementation.
 *
 * @author Rubenicos
 */
public class ServerCondition extends SingleSection.Text implements Condition {

    /**
     * Server condition reader.
     */
    public static final SectionReader<ServerCondition> READER = reader("server", ServerCondition::new);

    /**
     * Constructs a new ServerCondition.
     *
     * @param object a string representing the server name (e.g. "lobby")
     */
    public ServerCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        final Player player = context.get();
        return player.getCurrentServer()
                .map(server -> server.getServerInfo().getName().equals(getValue(context)))
                .orElse(false);
    }
}
