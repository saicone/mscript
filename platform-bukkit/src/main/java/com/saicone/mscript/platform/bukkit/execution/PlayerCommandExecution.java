package com.saicone.mscript.platform.bukkit.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        final Player player = context.get();
        final String command = getValue(context);

        context.sync(() -> Bukkit.dispatchCommand(player, command));

        return Result.DONE;
    }
}
