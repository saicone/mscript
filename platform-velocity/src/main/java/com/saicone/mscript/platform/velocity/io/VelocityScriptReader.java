package com.saicone.mscript.platform.velocity.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Execution;
import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.platform.velocity.VelocityConditions;
import com.saicone.mscript.platform.velocity.VelocityExecutions;
import com.saicone.mscript.platform.velocity.util.ConfigNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.IOException;

/**
 * Script reader implementation for Velocity platform.<br>
 * This reader allows to read scripts from a Velocity configuration
 * and convert single placeholders declarations into a pre-compiled placeholder value.
 *
 * @author Rubenicos
 */
public class VelocityScriptReader extends ScriptReader {

    public static final VelocityScriptReader INSTANCE = new VelocityScriptReader();

    /**
     * Constructs a new VelocityScriptReader with default condition and execution compilers.
     */
    public VelocityScriptReader() {
        this(new VelocitySectionCompiler<>(), new VelocitySectionCompiler<>());

        this.conditions.putAll(VelocityConditions.class);
        this.executions.putAll(VelocityExecutions.class);
    }

    /**
     * Constructs a new VelocityScriptReader with the given condition and execution compilers.
     *
     * @param conditions the condition compiler
     * @param executions the execution compiler
     */
    public VelocityScriptReader(@NotNull VelocitySectionCompiler<Condition> conditions, @NotNull VelocitySectionCompiler<Execution> executions) {
        super(conditions, executions);
    }

    @Override
    public @UnknownNullability Condition readCondition(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationNode) {
            return readCondition(ConfigNode.toMap((ConfigurationNode) object));
        }
        return super.readCondition(object);
    }

    @Override
    public @UnknownNullability Execution readExecution(@Nullable Object object) throws IOException {
        if (object instanceof ConfigurationNode) {
            return readExecution(ConfigNode.toMap((ConfigurationNode) object));
        }
        return super.readExecution(object);
    }
}
