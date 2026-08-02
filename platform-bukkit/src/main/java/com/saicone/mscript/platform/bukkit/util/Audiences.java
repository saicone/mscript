/*
 *  MIT License.
 *
 *  Copyright (c) 2025-2026 Rubenicos
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package com.saicone.mscript.platform.bukkit.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public final class Audiences {

    private static final boolean NATIVE_SUPPORT = Audience.class.isAssignableFrom(CommandSender.class);
    private static final class Provider {
        private static final Object INSTANCE = BukkitAudiences.create(PluginSource.unchecked());
    }

    @NotNull
    public static Audience all() {
        if (NATIVE_SUPPORT) {
            return (Audience) Bukkit.getServer();
        } else {
            return ((BukkitAudiences) Provider.INSTANCE).all();
        }
    }

    @NotNull
    public static Audience console() {
        if (NATIVE_SUPPORT) {
            return (Audience) Bukkit.getConsoleSender();
        } else {
            return ((BukkitAudiences) Provider.INSTANCE).console();
        }
    }

    @NotNull
    public static Audience player(@NotNull Player player) {
        if (NATIVE_SUPPORT) {
            return (Audience) player;
        } else {
            return ((BukkitAudiences) Provider.INSTANCE).player(player);
        }
    }

    @NotNull
    public static Audience sender(@NotNull CommandSender sender) {
        if (NATIVE_SUPPORT) {
            return (Audience) sender;
        } else {
            return ((BukkitAudiences) Provider.INSTANCE).sender(sender);
        }
    }

    @NotNull
    public static Audience world(@NotNull World world) {
        if (NATIVE_SUPPORT) {
            return (Audience) world;
        } else {
            try {
                final NamespacedKey key = world.getKey();
                return ((BukkitAudiences) Provider.INSTANCE).world(Key.key(key.getNamespace(), key.getKey()));
            } catch (NoSuchMethodError e) {
                return ((BukkitAudiences) Provider.INSTANCE).world(Key.key("minecraft", world.getName()));
            }
        }
    }

    Audiences() {
    }
}
