package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Condition;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.condition.BukkitPermissionCondition;
import com.saicone.mscript.platform.bukkit.condition.WorldCondition;

public final class BukkitConditions {

    public static final SectionReader<? extends Condition> PERMISSION = BukkitPermissionCondition.READER;
    public static final SectionReader<? extends Condition> WORLD = WorldCondition.READER;

    BukkitConditions() {
    }
}
