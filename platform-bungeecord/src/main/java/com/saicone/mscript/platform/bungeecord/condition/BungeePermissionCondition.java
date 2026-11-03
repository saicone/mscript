package com.saicone.mscript.platform.bungeecord.condition;

import com.saicone.mscript.Context;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.io.SectionReader;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Bungeecord implementation of {@link PermissionCondition}.
 */
public class BungeePermissionCondition extends PermissionCondition {

    /**
     * Bungeecord permission condition reader.
     */
    public static final SectionReader<BungeePermissionCondition> READER = reader(PermissionCondition.READER.regex(), BungeePermissionCondition::new);

    /**
     * Constructs a new BungeePermissionCondition.
     *
     * @param object a string representing the permission (e.g. "myplugin.mypermission")
     */
    public BungeePermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        final CommandSender sender = context.get();
        return sender.hasPermission(permission);
    }
}
