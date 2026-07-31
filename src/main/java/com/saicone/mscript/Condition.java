package com.saicone.mscript;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Condition extends Section {

    @Nullable
    Boolean test(@NotNull Context context);
}
