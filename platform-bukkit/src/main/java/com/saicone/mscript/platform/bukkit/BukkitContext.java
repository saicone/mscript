package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import com.saicone.mscript.context.AbstractComposedContext;
import com.saicone.mscript.platform.bukkit.util.Audiences;
import com.saicone.mscript.platform.bukkit.util.PAPI;
import com.saicone.mscript.util.Lazy;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class BukkitContext extends AbstractComposedContext implements Context {

    private static final Lazy<Boolean> MULTITHREADING = Lazy.of(() -> {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (Throwable t) {
            return false;
        }
    });

    protected final Plugin plugin;
    protected final CommandSender source;
    protected CommandSender agent;

    public BukkitContext(@NotNull Plugin plugin, @NotNull CommandSender source, @Nullable CommandSender agent) {
        this.plugin = plugin;
        this.source = source;
        this.agent = agent;
    }

    @NotNull
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public @NotNull CommandSender source() {
        return source;
    }

    @Override
    public @Nullable CommandSender agent() {
        return agent;
    }

    @Override
    public @NotNull Audience audience() {
        return Audiences.sender(source());
    }

    @Override
    public @NotNull Audience pointer() {
        return Audiences.sender(get());
    }

    @Override
    public @NotNull UUID getUniqueId() {
        final CommandSender sender = get();
        if (sender instanceof Entity player) {
            return player.getUniqueId();
        } else {
            return Context.SERVER_ID;
        }
    }

    @Override
    public @NotNull String parse(@NotNull String str) {
        String result = str;
        if (PAPI.get().isPresent() && str.contains("%")) {
            final CommandSender sender = get();
            result = PlaceholderAPI.setPlaceholders(sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null, str);
        }
        return result;
    }

    @Override
    public void sync(@NotNull Runnable command) {
        if (MULTITHREADING.get()) {
            Bukkit.getGlobalRegionScheduler().run(this.plugin, task -> command.run());
        } else if (Bukkit.isPrimaryThread()) {
            command.run();
        } else {
            Bukkit.getScheduler().runTask(this.plugin, command);
        }
    }

    @Override
    public void async(@NotNull Runnable command) {
        if (MULTITHREADING.get()) {
            if (this.source instanceof Entity entity) {
                entity.getScheduler().run(this.plugin, task -> command.run(), null);
            } else {
                Bukkit.getAsyncScheduler().runNow(this.plugin, task -> command.run());
            }
        } else if (Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, command);
        } else {
            command.run();
        }
    }

    @Override
    public void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        if (MULTITHREADING.get()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, task -> command.run(), ticks(time, unit));
        } else {
            Bukkit.getScheduler().runTaskLater(this.plugin, command, ticks(time, unit));
        }
    }

    @Override
    public void delayAsync(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        if (MULTITHREADING.get()) {
            if (this.source instanceof Entity entity) {
                entity.getScheduler().runDelayed(this.plugin, task -> command.run(), null, ticks(time, unit));
            } else {
                Bukkit.getAsyncScheduler().runDelayed(this.plugin, task -> command.run(), time, unit);
            }
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, command, ticks(time, unit));
        }
    }

    @Override
    public @NotNull ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        throw new IllegalStateException("The current context does not support parser override, use #composed() method instead");
    }

    @Override
    public @NotNull ComposedContext replace(@NotNull String str, @NotNull Object value) {
        throw new IllegalStateException("The current context does not support literal replacement, use #composed() method instead");
    }

    private static long ticks(long time, @NotNull TimeUnit unit) {
        final long ticks = unit.toMillis(time) / 50;
        if (ticks <= 0) {
            throw new IllegalArgumentException("Delay time must be greater than 0");
        }
        return ticks;
    }
}
