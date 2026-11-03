package com.saicone.mscript.platform.bungeecord.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Execution;
import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.platform.bungeecord.BungeeConditions;
import com.saicone.mscript.platform.bungeecord.BungeeExecutions;
import com.saicone.mscript.platform.bungeecord.util.Config;
import net.md_5.bungee.config.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;

/**
 * Script reader implementation for Bungeecord platform.<br>
 * This reader allows to read scripts from a Bungeecord configuration
 * and convert single placeholders declarations into a pre-compiled placeholder value.
 *
 * @author Rubenicos
 */
public class BungeeScriptReader extends ScriptReader {

    public static final BungeeScriptReader INSTANCE = new BungeeScriptReader();

    /**
     * Constructs a new BungeeScriptReader with default condition and execution compilers.
     */
    public BungeeScriptReader() {
        this(new BungeeSectionCompiler<>(), new BungeeSectionCompiler<>());

        this.conditions.putAll(BungeeConditions.class);
        this.executions.putAll(BungeeExecutions.class);
    }

    /**
     * Constructs a new BungeeScriptReader with the given condition and execution compilers.
     *
     * @param conditions the condition compiler
     * @param executions the execution compiler
     */
    public BungeeScriptReader(@NotNull BungeeSectionCompiler<Condition> conditions, @NotNull BungeeSectionCompiler<Execution> executions) {
        super(conditions, executions);
    }

    @Override
    public @UnknownNullability Condition readCondition(@Nullable Object object) throws IOException {
        if (object instanceof Configuration) {
            return readCondition(Config.toMap((Configuration) object));
        }
        return super.readCondition(object);
    }

    @Override
    public @UnknownNullability Execution readExecution(@Nullable Object object) throws IOException {
        if (object instanceof Configuration) {
            return readExecution(Config.toMap((Configuration) object));
        }
        return super.readExecution(object);
    }
}
