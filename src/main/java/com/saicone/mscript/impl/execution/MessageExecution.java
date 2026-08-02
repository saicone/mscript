package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Mini;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageExecution extends SingleSection.TextList implements Execution {

    public static final SectionReader<MessageExecution> READER = reader("(mini-?)?messages?|msg|tell", MessageExecution::new);

    public MessageExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        for (String line : getValue(context)) {
            context.audience().sendMessage(Mini.get().parse(context.audience(), line));
        }
        return Result.DONE;
    }
}
