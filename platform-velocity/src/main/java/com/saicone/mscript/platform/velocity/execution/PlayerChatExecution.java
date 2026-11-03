package com.saicone.mscript.platform.velocity.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player chat execution implementation.
 *
 * @author Rubenicos
 */
public class PlayerChatExecution extends SingleSection.Text implements Execution {

    /**
     * Player chat execution reader.
     */
    public static final SectionReader<PlayerChatExecution> READER = reader("(force-?)?(player-?)?(chat|say)", PlayerChatExecution::new);

    /**
     * Constructs a new player chat execution.
     *
     * @param object a string representing the message to send
     */
    public PlayerChatExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final Player player = context.get();
        final String message = getValue(context);

        player.spoofChatInput(message);

        return Result.DONE;
    }
}
