package com.saicone.mscript.platform.velocity.condition;

import com.saicone.mscript.Context;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.io.SectionReader;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.permission.Tristate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Velocity implementation of {@link PermissionCondition}.
 */
public class VelocityPermissionCondition extends PermissionCondition {

    /**
     * Velocity permission condition reader.
     */
    public static final SectionReader<VelocityPermissionCondition> READER = reader(PermissionCondition.READER.regex(), VelocityPermissionCondition::new);

    /**
     * Constructs a new VelocityPermissionCondition.
     *
     * @param object a string representing the permission (e.g. "myplugin.mypermission")
     */
    public VelocityPermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        final CommandSource source = context.get();
        final Tristate result = source.getPermissionValue(permission);
        return result == Tristate.UNDEFINED ? null : result.asBoolean();
    }
}
