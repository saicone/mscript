package com.saicone.mscript.context;

import com.saicone.mscript.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractContext<T> implements Context {

    protected final T source;
    protected T agent;

    public AbstractContext(@NotNull T source, @Nullable T agent) {
        this.source = source;
        this.agent = agent;
    }

    @Override
    public @NotNull T source() {
        return source;
    }

    @Override
    public @Nullable T agent() {
        return agent;
    }
}
