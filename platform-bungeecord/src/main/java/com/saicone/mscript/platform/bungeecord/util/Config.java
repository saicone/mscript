package com.saicone.mscript.platform.bungeecord.util;

import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@ApiStatus.Internal
public final class Config {

    @NotNull
    public static Configuration fromMap(@NotNull Map<?, ?> map) {
        final Configuration config = new Configuration();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> subMap) {
                config.set(String.valueOf(entry.getKey()), fromMap(subMap));
            } else {
                config.set(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return config;
    }

    @NotNull
    public static Map<String, Object> toMap(@NotNull Configuration config) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (String key : config.getKeys()) {
            Object value = config.get(key);
            if (value instanceof Configuration) {
                map.put(key, toMap((Configuration) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    Config() {
    }
}
