package com.saicone.mscript.impl.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.util.Tristate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PermissionCondition extends SingleSection.Text implements Condition {

    public static final SectionReader<PermissionCondition> READER = reader("(has-?)?perm(ission)?", PermissionCondition::new);

    public PermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        final UUID uniqueId = context.getUniqueId();
        if (uniqueId == Context.SERVER_ID) {
            return true;
        }

        final var user = LuckPermsProvider.get().getUserManager().getUser(uniqueId);
        if (user == null) {
            return null;
        }

        final var data = user.getCachedData().getPermissionData();
        final Tristate tristate = data.checkPermission(getValue(context));
        return tristate == Tristate.UNDEFINED ? null : tristate.asBoolean();
    }
}
