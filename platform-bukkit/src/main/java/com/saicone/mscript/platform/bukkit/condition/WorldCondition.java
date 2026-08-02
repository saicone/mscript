package com.saicone.mscript.platform.bukkit.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldCondition extends SingleSection.Text implements Condition {

    public static final SectionReader<WorldCondition> READER = reader("world", WorldCondition::new);

    public WorldCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        final Player player = context.get();
        return player.getWorld().getName().equals(getValue(context));
    }
}
