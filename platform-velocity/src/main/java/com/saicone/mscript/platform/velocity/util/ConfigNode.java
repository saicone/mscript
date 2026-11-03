package com.saicone.mscript.platform.velocity.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.LinkedHashMap;
import java.util.Map;

@ApiStatus.Internal
public final class ConfigNode {

    @NotNull
    public static ConfigurationNode fromMap(@NotNull Map<?, ?> map) {
        ConfigurationNode node = BasicConfigurationNode.root();
        try {
            node.set(map);
        } catch (Throwable t) {
            throw new RuntimeException("Failed to convert map to ConfigurationNode", t);
        }
        return node;
    }

    @NotNull
    public static Map<String, Object> toMap(@NotNull ConfigurationNode node) {
        final Map<String, Object> map = new LinkedHashMap<>();

        final Map<Object, ? extends ConfigurationNode> children = node.childrenMap();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : children.entrySet()) {
            if (entry.getValue().isMap()) {
                map.put(String.valueOf(entry.getKey()), toMap(entry.getValue()));
            } else {
                map.put(String.valueOf(entry.getKey()), entry.getValue().raw());
            }
        }

        return map;
    }

    ConfigNode() {
    }
}
