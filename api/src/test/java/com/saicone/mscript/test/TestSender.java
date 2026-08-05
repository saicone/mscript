package com.saicone.mscript.test;

import com.saicone.mscript.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestSender extends TestAudience {

    private static final Set<String> TRUE_PERMISSIONS = Set.of(
            "mscript.test.permission1",
            "mscript.test.permission2",
            "mscript.test.permission3",
            "mscript.test.permission4",
            "mscript.test.permission5"
    );
    private static final Set<String> FALSE_PERMISSIONS = Set.of(
            "mscript.test.permission6",
            "mscript.test.permission7",
            "mscript.test.permission8",
            "mscript.test.permission9",
            "mscript.test.permission10"
    );

    private static final TestSender SERVER = new TestSender(Context.SERVER_ID, "@server") {
        @Override
        public @NotNull Boolean hasPermission(@NotNull String permission) {
            return true;
        }
    };
    private static final Map<UUID, TestSender> PLAYERS = new HashMap<>();

    @NotNull
    public static TestSender server() {
        return SERVER;
    }

    @NotNull
    public static TestSender player(@NotNull String name) {
        return player(UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8)), name);
    }

    @NotNull
    public static TestSender player(@NotNull UUID uniqueId, @NotNull String name) {
        return PLAYERS.computeIfAbsent(uniqueId, id -> new TestSender(id, name));
    }

    private final UUID uniqueId;
    private final String name;

    protected TestSender(@NotNull UUID uniqueId, @NotNull String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Nullable
    public Boolean hasPermission(@NotNull String permission) {
        if (TRUE_PERMISSIONS.contains(permission)) {
            return Boolean.TRUE;
        } else if (FALSE_PERMISSIONS.contains(permission)) {
            return Boolean.FALSE;
        }
        return null;
    }

    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @NotNull
    public String getName() {
        return name;
    }
}
