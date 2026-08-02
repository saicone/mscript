package com.saicone.mscript.platform.bukkit.util;

import com.saicone.mscript.util.Lazy;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApiStatus.Internal
public final class PAPI {

    public static PAPI INSTANCE = new PAPI();

    @NotNull
    public static PAPI get() {
        return INSTANCE;
    }

    private final Lazy<Boolean> present = Lazy.of(() -> Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"));

    public boolean isPresent() {
        return present.get();
    }

    public boolean isPlaceholderValue(@NotNull String s) {
        return s.length() >= 2 &&
                s.startsWith("%") &&
                s.endsWith("%") &&
                s.indexOf('%', 1) == s.length() - 1;
    }

    public boolean contains(@NotNull String s) {
        if (isPresent()) {
            return contains(PlaceholderAPI.getPlaceholderPattern(), s);
        } else {
            return false;
        }
    }

    public boolean containsBracket(@NotNull String s) {
        if (isPresent()) {
            return contains(PlaceholderAPI.getBracketPlaceholderPattern(), s);
        } else {
            return false;
        }
    }

    private boolean contains(@NotNull Pattern pattern, @NotNull String s) {
        final Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            String match = matcher.group(1);
            final int index = match.indexOf('_');
            if (index == 0) {
                continue;
            } else if (index > 0) {
                match = match.substring(0, index);
            }
            if (PlaceholderAPI.isRegistered(match)) {
                return true;
            }
        }
        return false;
    }
}
