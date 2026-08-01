package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.MTypes;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionBarExecution extends SingleSection<Component> implements Execution {

    public static final SectionReader<ActionBarExecution> READER = reader("action-bar", ActionBarExecution::new);

    public ActionBarExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    protected @NotNull Component parse(@NotNull Object object) {
        return MTypes.COMPONENT.parse(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        context.audience().sendActionBar(getValue(context));
        return Result.DONE;
    }
}
