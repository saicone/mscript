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

public class BukkitSectionCompiler<S extends Section> extends SectionCompiler<S> {

    public BukkitSectionCompiler() {
        this(new LinkedHashSet<>());
    }

    public BukkitSectionCompiler(@NotNull Set<SectionReader<?>> readers) {
        super(readers);
    }

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
