package com.saicone.mscript.platform.bungeecord;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import com.saicone.mscript.context.AbstractComposedContext;
import com.saicone.mscript.platform.bungeecord.util.Audiences;
import com.saicone.mscript.platform.bungeecord.util.PluginSource;
import com.saicone.mscript.util.Mini;
import net.kyori.adventure.audience.Audience;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * Context implementation for Bungeecord platform.<br>
 * This context is used to execute scripts in a Bungeecord environment, providing access to the plugin,
 * command sender, and other Bungeecord-specific features.
 *
 * @author Rubenicos
 */
public class BungeeContext extends AbstractComposedContext implements Context {

    /**
     * Creates a new BungeeContext with the specified command sender as the source.
     *
     * @param sender the command sender that is the source of the script execution
     * @return       a newly generated BungeeContext instance
     */
    @NotNull
    public static BungeeContext of(@NotNull CommandSender sender) {
        return new BungeeContext(PluginSource.unchecked(), sender, null);
    }

    /**
     * Creates a new BungeeContext with the specified plugin, source, and agent.
     *
     * @param plugin the plugin associated with this context
     * @param sender the command sender that is the source of the script execution
     * @return       a newly generated BungeeContext instance
     */
    @NotNull
    public static BungeeContext of(@NotNull Plugin plugin, @NotNull CommandSender sender) {
        return new BungeeContext(plugin, sender, null);
    }

    protected final Plugin plugin;
    protected final CommandSender source;
    protected CommandSender agent;

    /**
     * Constructs a new BungeeContext with the specified plugin, source, and agent.
     *
     * @param plugin the plugin associated with this context
     * @param source the command sender that is the source of the script execution
     * @param agent  the command sender that is the agent of the script execution, can be null
     */
    public BungeeContext(@NotNull Plugin plugin, @NotNull CommandSender source, @Nullable CommandSender agent) {
        this.plugin = plugin;
        this.source = source;
        this.agent = agent;

        attribute("context", this);
        attribute("plugin", plugin);
        if (source instanceof ProxiedPlayer player) {
            attribute("player", player);
        }
        attribute("server", ProxyServer.getInstance());

        function("minimessage", object -> Mini.get().parse(pointer(), object));
    }

    /**
     * Get the plugin associated with this context.
     *
     * @return a plugin instance
     */
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
        if (sender instanceof ProxiedPlayer player) {
            return player.getUniqueId();
        } else {
            return Context.SERVER_ID;
        }
    }

    @Override
    public void async(@NotNull Runnable command) {
        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, command);
    }

    @Override
    public void delayAsync(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        ProxyServer.getInstance().getScheduler().schedule(this.plugin, command, time, unit);
    }

    @Override
    public @NotNull ComposedContext parser(@NotNull UnaryOperator<String> operator) {
        throw new IllegalStateException("The current context does not support parser override, use #composed() method instead");
    }

    @Override
    public @NotNull ComposedContext replace(@NotNull String str, @NotNull Object value) {
        throw new IllegalStateException("The current context does not support literal replacement, use #composed() method instead");
    }
}
