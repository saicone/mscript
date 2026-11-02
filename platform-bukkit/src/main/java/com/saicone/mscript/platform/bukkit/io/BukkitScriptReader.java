package com.saicone.mscript.platform.bukkit.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Value;
import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.platform.bukkit.BukkitConditions;
import com.saicone.mscript.platform.bukkit.BukkitExecutions;
import com.saicone.mscript.platform.bukkit.util.ConfigSection;
import com.saicone.mscript.platform.bukkit.util.PAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;

/**
 * Script reader implementation for Bukkit platform.<br>
 * This reader allows to read scripts from a Bukkit configuration
 * and convert single placeholders declarations into a pre-compiled placeholder value.
 *
 * @author Rubenicos
 */
public class BukkitScriptReader extends ScriptReader {

    public static final BukkitScriptReader INSTANCE = new BukkitScriptReader();

    /**
     * Constructs a new BukkitScriptReader with default condition and execution compilers.
     */
    public BukkitScriptReader() {
        this(new BukkitSectionCompiler<>(), new BukkitSectionCompiler<>());

        this.conditions.putAll(BukkitConditions.class);
        this.executions.putAll(BukkitExecutions.class);
    }

    /**
     * Constructs a new BukkitScriptReader with the given condition and execution compilers.
     *
     * @param conditions the condition compiler
     * @param executions the execution compiler
     */
    public BukkitScriptReader(@NotNull BukkitSectionCompiler<Condition> conditions, @NotNull BukkitSectionCompiler<Execution> executions) {
        super(conditions, executions);
    }

    @Override
    public @NotNull Value<?> readValue(@NotNull String s) {
        if (PAPI.get().isPresent() && PAPI.get().isPlaceholderValue(s)) {
            final String[] parts = s.substring(1, s.length() - 1).split("_", 2);
            return context -> {
                final PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(parts[0]);
                if (expansion != null) {
                    final Object object = context.get();
                    return expansion.onRequest(object instanceof OfflinePlayer ? (OfflinePlayer) object : null, parts.length > 1 ? parts[1] : "");
                }
                return s;
            };
        }
        return super.readValue(s);
    }

    @Override
    public @UnknownNullability Condition readCondition(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationSection) {
            return readCondition(ConfigSection.toMap((ConfigurationSection) object));
        }
        return super.readCondition(object);
    }

    @Override
    public @UnknownNullability Execution readExecution(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationSection) {
            return readExecution(ConfigSection.toMap((ConfigurationSection) object));
        }
        return super.readExecution(object);
    }
}
