package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Context;
import com.saicone.mscript.context.AbstractContext;
import com.saicone.mscript.platform.bukkit.util.Audiences;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BukkitContext extends AbstractContext {

    private final Plugin plugin;

    public BukkitContext(@NotNull Plugin plugin, @NotNull CommandSender source, @Nullable CommandSender agent) {
        super(source, agent);
        this.plugin = plugin;
    }

    @NotNull
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public @NotNull Audience audience() {
        return Audiences.sender(get());
    }

    @Override
    public @NotNull UUID getUniqueId() {
        final CommandSender sender = get();
        if (sender instanceof Player player) {
            return player.getUniqueId();
        } else {
            return Context.SERVER_ID;
        }
    }

    // TODO: Add multi-threaded server support

    @Override
    public void sync(@NotNull Runnable command) {
        if (Bukkit.isPrimaryThread()) {
            command.run();
        } else {
            Bukkit.getScheduler().runTask(this.plugin, command);
        }
    }

    @Override
    public void async(@NotNull Runnable command) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, command);
        } else {
            command.run();
        }
    }

    @Override
    public void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        final long ticks = unit.toMillis(time) / 50;
        if (ticks <= 0) {
            throw new IllegalArgumentException("Delay time must be greater than 0");
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, command, ticks);
    }
}
