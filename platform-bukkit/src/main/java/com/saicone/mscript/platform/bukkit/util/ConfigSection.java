package com.saicone.mscript.platform.bukkit.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

@ApiStatus.Internal
public final class ConfigSection extends MemoryConfiguration {

    @NotNull
    public static ConfigurationSection fromMap(@NotNull Map<?, ?> map) {
        final ConfigSection section = new ConfigSection();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> subMap) {
                section.set(String.valueOf(entry.getKey()), fromMap(subMap));
            } else {
                section.set(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        return section;
    }

    @NotNull
    public static Map<String, Object> toMap(@NotNull ConfigurationSection section) {
        final Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                map.put(key, toMap((ConfigurationSection) value));
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    ConfigSection() {
        super();
    }
}
