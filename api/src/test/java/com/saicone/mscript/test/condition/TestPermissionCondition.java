package com.saicone.mscript.test.condition;

import com.saicone.mscript.Context;
import com.saicone.mscript.impl.condition.PermissionCondition;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.test.TestSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestPermissionCondition extends PermissionCondition {

    public static final SectionReader<TestPermissionCondition> READER = reader(PermissionCondition.READER.regex(), TestPermissionCondition::new);

    public TestPermissionCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean testPlatform(@NotNull Context context, @NotNull String permission) {
        if (context.getUniqueId().equals(Context.SERVER_ID)) {
            return true;
        } else {
            final TestSender sender = context.get();
            return sender.hasPermission(permission);
        }
    }
}
