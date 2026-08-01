package com.saicone.mscript.context;

import com.saicone.mscript.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractContext implements Context {

    protected final Object source;
    protected Object agent;

    public AbstractContext(@NotNull Object source, @Nullable Object agent) {
        this.source = source;
        this.agent = agent;
    }

    @Override
    public @NotNull Object source() {
        return source;
    }

    @Override
    public @Nullable Object agent() {
        return agent;
    }
}
