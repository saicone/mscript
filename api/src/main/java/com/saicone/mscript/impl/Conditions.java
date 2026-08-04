package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.condition.ChanceCondition;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.impl.condition.PlayerPlatformCondition;
import com.saicone.mscript.io.SectionReader;

@SuppressWarnings("all")
public final class Conditions {

    public static final SectionReader<? extends Condition> CHANCE = ChanceCondition.READER;
    public static final SectionReader<? extends Condition> PERMISSION = PermissionCondition.READER;
    public static final SectionReader<? extends Condition> PLAYER_PLATFORM = PlayerPlatformCondition.READER;

    public static final SectionReader<Condition> PLAYER = SectionReader.unary("(is-?)?player", id -> {
        return context -> !context.getUniqueId().equals(Context.SERVER_ID);
    });
    public static final SectionReader<Condition> CONSOLE = SectionReader.unary("(is-?)?console", id -> {
        return context -> context.getUniqueId().equals(Context.SERVER_ID);
    });

    Conditions() {
    }
}
