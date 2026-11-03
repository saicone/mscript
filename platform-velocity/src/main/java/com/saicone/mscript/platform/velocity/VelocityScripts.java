package com.saicone.mscript.platform.velocity;

import com.saicone.mscript.Execution;
import com.saicone.mscript.platform.velocity.io.VelocityScriptReader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Utility class for compiling scripts into executions.
 *
 * @author Rubenicos
 */
public final class VelocityScripts {

    /**
     * Compiles the given object into an execution.
     *
     * @param object the object to compile.
     * @return       the compiled execution.
     */
    @UnknownNullability
    public static Execution compile(@Nullable Object object) {
        try {
            return VelocityScriptReader.INSTANCE.readExecution(object);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    VelocityScripts() {
    }
}
