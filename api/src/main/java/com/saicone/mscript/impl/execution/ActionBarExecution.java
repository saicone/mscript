package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Mini;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActionBarExecution extends SingleSection.Text implements Execution {

    public static final SectionReader<ActionBarExecution> READER = reader("action-?bar", ActionBarExecution::new);

    public ActionBarExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        context.audience().sendActionBar(Mini.get().parse(context.pointer(), getValue(context)));
        return Result.DONE;
    }
}
