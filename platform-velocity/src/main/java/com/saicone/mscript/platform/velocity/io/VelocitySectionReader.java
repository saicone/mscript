package com.saicone.mscript.platform.velocity.io;

import com.saicone.mscript.Section;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.util.ConfigNode;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

/**
 * Section reader implementation for Velocity platform.<br>
 * This reader allow to read sections from a Velocity configuration.
 *
 * @author Rubenicos
 *
 * @param <S>
 */
public class VelocitySectionReader<S extends Section> extends SectionReader<S> {

    /**
     * Constructs a new VelocitySectionReader with the given regex and default key {@code "value"}.
     *
     * @param regex the regex pattern to match identifiers
     */
    public VelocitySectionReader(@NotNull @Language("RegExp") String regex) {
        super(regex);
    }

    /**
     * Constructs a new VelocitySectionReader with the given regex and default key.
     *
     * @param regex      the regex pattern to match identifiers
     * @param defaultKey the default key to use when reading from a map
     */
    public VelocitySectionReader(@NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
        super(regex, defaultKey);
    }

    @Override
    public S read(@NotNull String id, @Nullable Object context) {
        if (context instanceof ConfigurationNode) {
            return read(id, (ConfigurationNode) context);
        }
        return super.read(id, context);
    }

    /**
     * Read a section with the given identifier and {@link ConfigurationNode} context.<br>
     * This method by default converts the configuration section to a map and calls {@link SectionReader#read(String, java.util.Map)} with it.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    protected S read(@NotNull String id, @NotNull ConfigurationNode context) {
        return super.read(id, ConfigNode.toMap(context));
    }
}
