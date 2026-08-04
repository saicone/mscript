package com.saicone.mscript.platform.bukkit.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerChatExecution extends SingleSection.Text implements Execution {

    public static final SectionReader<PlayerChatExecution> READER = reader("(force-?)?(player-?)?(chat|say)", PlayerChatExecution::new);

    public PlayerChatExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final Player player = context.get();
        final String message = getValue(context);

        player.chat(message);

        return Result.DONE;
    }
}