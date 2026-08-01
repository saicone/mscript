package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.impl.condition.ChanceCondition;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.io.SectionReader;

@SuppressWarnings("all")
public final class Conditions {

    public static final SectionReader<? extends Condition> CHANCE = ChanceCondition.READER;
    public static final SectionReader<? extends Condition> PERMISSION = PermissionCondition.READER;

    Conditions() {
    }
}
