package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Execution;
import com.saicone.mscript.platform.bukkit.io.BukkitScriptReader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public final class BukkitScripts {

    @UnknownNullability
    public static Execution compile(@Nullable Object object) {
        try {
            return BukkitScriptReader.INSTANCE.readExecution(object);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    BukkitScripts() {
    }
}
