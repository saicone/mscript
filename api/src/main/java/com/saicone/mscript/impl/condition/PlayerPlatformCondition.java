package com.saicone.mscript.impl.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Lazy;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Player platform condition implementation.
 *
 * @author Rubenicos
 */
public class PlayerPlatformCondition extends SingleSection.Text implements Condition {

    /**
     * Player platform condition reader.
     */
    public static final SectionReader<PlayerPlatformCondition> READER = reader("player-?platform", PlayerPlatformCondition::new);

    private final Lazy<Boolean> useFloodgate = Lazy.init(() -> {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            return true;
        } catch (Throwable t) {
            return false;
        }
    });

    /**
     * Constructs a new player platform condition.
     *
     * @param object a string representing the platform (e.g. "java", "bedrock", etc.)
     */
    public PlayerPlatformCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        final UUID uniqueId = context.getUniqueId();
        final String platform = getValue(context);

        if (uniqueId.equals(Context.SERVER_ID)) {
            return null; // not a player
        }

        return switch (platform.toLowerCase()) {
            case "bedrock" -> isBedrockPlayer(uniqueId);
            case "java" -> !isBedrockPlayer(uniqueId);
            case "java premium", "java online" -> !isBedrockPlayer(uniqueId) && uniqueId.version() == 4;
            case "java no-premium", "java offline" -> !isBedrockPlayer(uniqueId) && uniqueId.version() == 3;
            default -> throw new IllegalArgumentException("Unknown platform: '" + platform + "'");
        };
    }

    /**
     * Tests if the given unique ID belongs to a Bedrock player.
     *
     * @param uniqueId the unique ID to check
     * @return         true if the unique ID belongs to a Bedrock player, false otherwise
     */
    public boolean isBedrockPlayer(@NotNull UUID uniqueId) {
        if (useFloodgate.get()) {
            return FloodgateApi.getInstance().isFloodgatePlayer(uniqueId);
        } else {
            return uniqueId.toString().startsWith("00000000-0000-0000");
        }
    }
}
