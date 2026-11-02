package com.saicone.mscript.platform.bukkit.io;

import com.saicone.mscript.Section;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.util.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Section reader implementation for Bukkit platform.<br>
 * This reader allow to read sections from a Bukkit configuration.
 *
 * @author Rubenicos
 *
 * @param <S>
 */
public class BukkitSectionReader<S extends Section> extends SectionReader<S> {

    /**
     * Constructs a new BukkitSectionReader with the given regex and default key {@code "value"}.
     *
     * @param regex the regex pattern to match identifiers
     */
    public BukkitSectionReader(@NotNull @Language("RegExp") String regex) {
        super(regex);
    }

    /**
     * Constructs a new BukkitSectionReader with the given regex and default key.
     *
     * @param regex      the regex pattern to match identifiers
     * @param defaultKey the default key to use when reading from a map
     */
    public BukkitSectionReader(@NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
        super(regex, defaultKey);
    }

    @Override
    public S read(@NotNull String id, @Nullable Object context) {
        if (context instanceof ConfigurationSection) {
            return read(id, (ConfigurationSection) context);
        }
        return super.read(id, context);
    }

    /**
     * Read a section with the given identifier and {@link ConfigurationSection} context.<br>
     * This method by default converts the configuration section to a map and calls {@link SectionReader#read(String, java.util.Map)} with it.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    protected S read(@NotNull String id, @NotNull ConfigurationSection context) {
        return super.read(id, ConfigSection.toMap(context));
    }
}
