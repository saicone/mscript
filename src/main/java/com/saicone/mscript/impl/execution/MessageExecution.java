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

import java.util.List;

public class MessageExecution extends SingleSection<List<Component>> implements Execution {

    public static final SectionReader<MessageExecution> READER = reader("(mini-?)?messages?|msg|tell", MessageExecution::new);

    public MessageExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    protected @NotNull List<Component> parse(@NotNull Object object) {
        return MTypes.COMPONENT_LIST.parse(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        for (Component line : getValue(context)) {
            context.audience().sendMessage(line);
        }
        return Result.DONE;
    }
}
