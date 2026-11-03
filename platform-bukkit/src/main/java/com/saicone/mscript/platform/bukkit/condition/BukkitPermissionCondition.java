package com.saicone.mscript.platform.bukkit.condition;

import com.saicone.mscript.Context;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.io.SectionReader;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Bukkit implementation of {@link PermissionCondition}.
 */
public class BukkitPermissionCondition extends PermissionCondition {

    /**
     * Bukkit permission condition reader.
     */
    public static final SectionReader<BukkitPermissionCondition> READER = reader(PermissionCondition.READER.regex(), BukkitPermissionCondition::new);

    /**
     * Constructs a new BukkitPermissionCondition.
     *
     * @param object a string representing the permission (e.g. "myplugin.mypermission")
     */
    public BukkitPermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        final CommandSender sender = context.get();
        try {
            return sender.permissionValue(permission).toBoolean();
        } catch (Throwable t) {
            return sender.hasPermission(permission);
        }
    }
}
