package com.saicone.mscript.platform.velocity;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import com.saicone.mscript.context.AbstractComposedContext;
import com.saicone.mscript.util.Mini;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

/**
 * Context implementation for Velocity platform.<br>
 * This context is used to execute scripts in a Velocity environment, providing access to the proxy, plugin,
 * command sender, and other Velocity-specific features.
 *
 * @author Rubenicos
 */
public class VelocityContext extends AbstractComposedContext implements Context {

    /**
     * Creates a new VelocityContext with the specified plugin, source, and agent.
     *
     * @param proxy  the proxy server associated with this context
     * @param plugin the plugin associated with this context
     * @param sender the command sender that is the source of the script execution
     * @return       a newly generated VelocityContext instance
     */
    @NotNull
    public static VelocityContext of(@NotNull ProxyServer proxy, @NotNull Object plugin, @NotNull CommandSource sender) {
        return new VelocityContext(proxy, plugin, sender, null);
    }

    protected final ProxyServer proxy;
    protected final Object plugin;
    protected final CommandSource source;
    protected CommandSource agent;

    /**
     * Constructs a new VelocityContext with the specified plugin, source, and agent.
     *
     * @param proxy  the proxy server associated with this context
     * @param plugin the plugin associated with this context
     * @param source the command sender that is the source of the script execution
     * @param agent  the command sender that is the agent of the script execution, can be null
     */
    public VelocityContext(@NotNull ProxyServer proxy, @NotNull Object plugin, @NotNull CommandSource source, @Nullable CommandSource agent) {
        this.proxy = proxy;
        this.plugin = plugin;
        this.source = source;
        this.agent = agent;

        attribute("context", this);
        attribute("plugin", plugin);
        if (source instanceof Player player) {
            attribute("player", player);
        }
        attribute("server", proxy);

        function("minimessage", object -> Mini.get().parse(pointer(), object));
    }

    /**
     * Get the proxy server associated with this context.
     *
     * @return a proxy server instance
     */
    @NotNull
    public ProxyServer proxy() {
        return proxy;
    }

    /**
     * Get the plugin associated with this context.
     *
     * @return a plugin instance
     */
    @NotNull
    public Object plugin() {
        return plugin;
    }

    @Override
    public @NotNull CommandSource source() {
        return source;
    }

    @Override
    public @Nullable CommandSource agent() {
        return agent;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        final CommandSource sender = get();
        if (sender instanceof Player player) {
            return player.getUniqueId();
        } else {
            return Context.SERVER_ID;
        }
    }

    @Override
    public void async(@NotNull Runnable command) {
        this.proxy.getScheduler().buildTask(this.plugin, command).schedule();
    }

    @Override
    public void delayAsync(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        this.proxy.getScheduler().buildTask(this.plugin, command).delay(time, unit).schedule();
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
