package com.saicone.mscript.platform.bukkit.io;

import com.saicone.mscript.Section;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.util.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitSectionReader<S extends Section> extends SectionReader<S> {

    public BukkitSectionReader(@NotNull @Language("RegExp") String regex) {
        super(regex);
    }

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

    protected S read(@NotNull String id, @NotNull ConfigurationSection context) {
        return super.read(id, ConfigSection.toMap(context));
    }
}
