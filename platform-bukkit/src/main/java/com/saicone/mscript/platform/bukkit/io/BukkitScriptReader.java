package com.saicone.mscript.platform.bukkit.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Execution;
import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.platform.bukkit.BukkitConditions;
import com.saicone.mscript.platform.bukkit.BukkitExecutions;
import com.saicone.mscript.platform.bukkit.util.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class BukkitScriptReader extends ScriptReader {

    public static final BukkitScriptReader INSTANCE = new BukkitScriptReader();

    public BukkitScriptReader() {
        this(new BukkitSectionCompiler<>(), new BukkitSectionCompiler<>());
    }

    public BukkitScriptReader(@NotNull BukkitSectionCompiler<Condition> conditions, @NotNull BukkitSectionCompiler<Execution> executions) {
        super(conditions, executions);

        this.conditions.putAll(BukkitConditions.class);
        this.executions.putAll(BukkitExecutions.class);
    }

    @Override
    public @Nullable Condition readCondition(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationSection) {
            return readCondition(ConfigSection.toMap((ConfigurationSection) object));
        }
        return super.readCondition(object);
    }

    @Override
    public @Nullable Execution readExecution(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationSection) {
            return readExecution(ConfigSection.toMap((ConfigurationSection) object));
        }
        return super.readExecution(object);
    }
}
