package com.saicone.mscript.impl.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Lazy;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Permission condition implementation.
 *
 * @author Rubenicos
 */
public class PermissionCondition extends SingleSection.Text implements Condition {

    /**
     * Permission condition reader.
     */
    public static final SectionReader<PermissionCondition> READER = reader("(has-?)?perm(ission)?", PermissionCondition::new);

    private final Lazy<Boolean> useLuckPerms = Lazy.init(() -> {
        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
            return true;
        } catch (Throwable t) {
            return false;
        }
    });

    /**
     * Constructs a new permission condition.
     *
     * @param object a string representing the permission (e.g. "myplugin.mypermission")
     */
    public PermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        final String permission = getValue(context);
        if (permission == null || permission.isBlank()) {
            return true;
        }
        if (useLuckPerms.get()) {
            return test(context, permission);
        } else {
            return testPlatform(context, permission);
        }
    }

    /**
     * Tests if the given context has the specified permission using LuckPerms.
     *
     * @param context    the context to check the permission for
     * @param permission the permission to check
     * @return           true if the context has the permission, false if not, or null if undefined
     */
    @Nullable
    public final Boolean test(@NotNull Context context, @NotNull String permission) {
        final UUID uniqueId = context.getUniqueId();
        if (uniqueId == Context.SERVER_ID) {
            return true;
        }

        final var user = LuckPermsProvider.get().getUserManager().getUser(uniqueId);
        if (user == null) {
            return null;
        }

        final var data = user.getCachedData().getPermissionData();
        final Tristate tristate = data.checkPermission(permission);
        return tristate == Tristate.UNDEFINED ? null : tristate.asBoolean();
    }

    /**
     * Tests if the given context has the specified permission using platform-specific methods.
     *
     * @param context    the context to check the permission for
     * @param permission the permission to check
     * @return           true if the context has the permission, false if not, or null if undefined
     */
    @Nullable
    public Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        throw new UnsupportedOperationException("Platform-specific permission condition is not implemented. Implement PermissionCondition or use LuckPerms");
    }
}
