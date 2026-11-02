package com.saicone.mscript.platform.bukkit.io;

import com.saicone.mscript.Section;
import com.saicone.mscript.io.SectionCompiler;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.util.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Section compiler implementation for Bukkit platform.<br>
 * This compiler allows to compile sections from a Bukkit configuration.
 *
 * @author Rubenicos
 *
 * @param <S> the type of section to compile
 */
public class BukkitSectionCompiler<S extends Section> extends SectionCompiler<S> {

    /**
     * Constructs a new BukkitSectionCompiler with an empty set of readers.
     */
    public BukkitSectionCompiler() {
        this(new LinkedHashSet<>());
    }

    /**
     * Constructs a new BukkitSectionCompiler with the given set of readers.
     *
     * @param readers the set of readers to use
     */
    public BukkitSectionCompiler(@NotNull Set<SectionReader<?>> readers) {
        super(readers);
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link ConfigurationSection} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<S> compileConfig(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<ConfigurationSection, S> function) {
        final SectionReader<S> reader = reader(regex, (id, context) -> {
            if (context instanceof ConfigurationSection) {
                return function.apply(id, (ConfigurationSection) context);
            } else if (context instanceof Map<?, ?>) {
                return function.apply(id, ConfigSection.fromMap((Map<?, ?>) context));
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    @Override
    protected @NotNull SectionReader<S> reader(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Object, S> function) {
        return new BukkitSectionReader<S>(regex) {
            @Override
            public S read(@NotNull String id, @Nullable Object context) {
                return function.apply(id, context);
            }
        };
    }
}
