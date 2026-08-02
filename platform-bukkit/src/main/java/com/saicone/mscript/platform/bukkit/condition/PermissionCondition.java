package com.saicone.mscript.platform.bukkit.condition;

import com.saicone.mscript.Context;
import com.saicone.mscript.io.SectionReader;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PermissionCondition extends com.saicone.mscript.impl.condition.PermissionCondition {

    public static final SectionReader<PermissionCondition> READER = reader(com.saicone.mscript.impl.condition.PermissionCondition.READER.regex(), PermissionCondition::new);

    public PermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        final CommandSender sender = context.get();
        return sender.hasPermission(permission);
    }
}
