package com.saicone.mscript.platform.bukkit.execution;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.BukkitContext;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerExecution extends SingleSection.Text implements Execution {

    private static final String BUNGEECORD_CHANNEL = "BungeeCord";

    public static final SectionReader<ServerExecution> READER = reader("connect|proxy|bungee|velocity|server", ServerExecution::new);

    public ServerExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final Plugin plugin = ((BukkitContext) context).plugin();
        final Player player = context.get();
        final String server = getValue(context);

        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");  // Subchannel
        out.writeUTF(server);        // Server name

        player.sendPluginMessage(plugin, BUNGEECORD_CHANNEL, out.toByteArray());

        return Result.DONE;
    }
}