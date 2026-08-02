/*
 *  MIT License.
 *
 *  Copyright (c) 2026 Rubenicos
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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;

@ApiStatus.Internal
public final class PluginSource {

    public static Plugin DEFAULT = null;
    private static final Map<Class<?>, Plugin> CACHE = new HashMap<>();

    PluginSource() {
    }

    @NotNull
    public static Plugin unchecked() {
        try {
            return Objects.requireNonNull(get());
        } catch (Throwable t) {
            throw new RuntimeException("There was an error while trying to get the current plugin, consider using '" + PluginSource.class.getName() + ".DEFAULT = this' on your plugin initialization to avoid this error", t);
        }
    }

    @Nullable
    public static synchronized Plugin get() throws IOException, URISyntaxException {
        if (DEFAULT != null) {
            return DEFAULT;
        }
        return get(PluginSource.class);
    }

    @Nullable
    public static synchronized Plugin get(@NotNull Class<?> source) throws IOException, URISyntaxException {
        if (CACHE.containsKey(source)) {
            return CACHE.get(source);
        }

        final Plugin plugin;
        final CodeSource codeSource = source.getProtectionDomain().getCodeSource();
        final File file = new File(codeSource.getLocation().toURI());
        try (JarFile jar = new JarFile(file)) {
            final InputStream input = jar.getInputStream(jar.stream().filter(entry -> entry.getName().equals("plugin.yml")).findFirst().orElseThrow());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                final String pluginName = reader.lines()
                        .filter(line -> line.startsWith("name:"))
                        .map(line -> line.substring("name:".length()).split("#", 2)[0].trim())
                        .findFirst()
                        .orElseThrow();
                plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            }
        }

        CACHE.put(source, plugin);
        return plugin;
    }
}
