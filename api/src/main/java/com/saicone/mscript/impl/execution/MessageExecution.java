package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Mini;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message execution implementation.
 *
 * @author Rubenicos
 */
public class MessageExecution extends SingleSection.TextList implements Execution {

    /**
     * Message execution reader.
     */
    public static final SectionReader<MessageExecution> READER = reader("(mini-?)?messages?|msg|tell", MessageExecution::new);

    /**
     * Constructs a new message execution.
     *
     * @param object a string or list of strings representing the messages to send
     */
    public MessageExecution(@Nullable Object object) {
        super(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        for (String line : getValue(context)) {
            context.audience().sendMessage(Mini.get().parse(context.pointer(), line));
        }
        return Result.DONE;
    }
}
