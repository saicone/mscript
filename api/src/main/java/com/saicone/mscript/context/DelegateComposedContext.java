package com.saicone.mscript.context;

import com.saicone.mscript.ComposedContext;
import com.saicone.mscript.Context;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

@ApiStatus.Internal
public class DelegateComposedContext extends AbstractComposedContext implements DelegateContext, ComposedContext {

    private final Context delegate;

    public DelegateComposedContext(@NotNull Context delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull Context delegate() {
        return delegate;
    }

    @Override
    public void forEachBinding(@NotNull BiConsumer<String, Object> consumer) {
        if (delegate instanceof ComposedContext composed) {
            composed.forEachBinding(consumer);
        }
        super.forEachBinding(consumer);
    }

    @Override
    public void forEachAttribute(@NotNull BiConsumer<String, Object> consumer) {
        if (delegate instanceof ComposedContext composed) {
            composed.forEachAttribute(consumer);
        }
        super.forEachAttribute(consumer);
    }
}
