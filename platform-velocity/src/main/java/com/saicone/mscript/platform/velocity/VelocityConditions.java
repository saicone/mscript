package com.saicone.mscript.platform.velocity;

import com.saicone.mscript.Condition;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.condition.ServerCondition;
import com.saicone.mscript.platform.velocity.condition.VelocityPermissionCondition;

/**
 * A collection of built-in Velocity conditions.
 *
 * @author Rubenicos
 */
@SuppressWarnings("unused")
public final class VelocityConditions {

    public static final SectionReader<? extends Condition> PERMISSION = VelocityPermissionCondition.READER;
    public static final SectionReader<? extends Condition> WORLD = ServerCondition.READER;

    VelocityConditions() {
    }
}
