package com.saicone.mscript.platform.bungeecord.util;

import com.saicone.mscript.util.Lazy;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class Audiences {

    public static final Lazy<BungeeAudiences> INSTANCE = Lazy.mutable(() -> BungeeAudiences.create(PluginSource.unchecked()));

    @NotNull
    public static Audience all() {
        return INSTANCE.get().all();
    }

    @NotNull
    public static Audience console() {
        return INSTANCE.get().console();
    }

    @NotNull
    public static Audience player(@NotNull ProxiedPlayer player) {
        return INSTANCE.get().player(player);
    }

    @NotNull
    public static Audience sender(@NotNull CommandSender sender) {
        return INSTANCE.get().sender(sender);
    }

    @NotNull
    public static Audience server(@NotNull String name) {
        return INSTANCE.get().server(name);
    }

    Audiences() {
    }
}
