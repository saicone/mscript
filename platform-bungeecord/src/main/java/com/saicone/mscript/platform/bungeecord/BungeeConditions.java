package com.saicone.mscript.platform.bungeecord;

import com.saicone.mscript.Condition;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bungeecord.condition.BungeePermissionCondition;
import com.saicone.mscript.platform.bungeecord.condition.ServerCondition;

/**
 * A collection of built-in Bungeecord conditions.
 *
 * @author Rubenicos
 */
@SuppressWarnings("unused")
public final class BungeeConditions {

    public static final SectionReader<? extends Condition> PERMISSION = BungeePermissionCondition.READER;
    public static final SectionReader<? extends Condition> WORLD = ServerCondition.READER;

    BungeeConditions() {
    }
}
