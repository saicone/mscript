package com.saicone.mscript.platform.bukkit.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.util.Audiences;
import com.saicone.mscript.util.Mini;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BroadcastExecution extends SingleSection.TextList implements Execution {

    public static final SectionReader<BroadcastExecution> READER = reader("(mini-?)?broadcast-?(message|msg)?", BroadcastExecution::new);

    public BroadcastExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final CommandSender sender = context.get();
        if (sender instanceof Player && Mini.get().isUsingMiniPlaceholders()) {
            final List<String> lines = getValue(context);
            final Audience main = context.audience();
            for (Player player : Bukkit.getOnlinePlayers()) {
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
            final Audience audience = context.audience();
            for (String line : getValue(context)) {
                Audiences.all().sendMessage(Mini.get().parse(audience, line));
            }
        }

        return Result.DONE;
    }
}