package com.saicone.mscript.util;

import com.saicone.types.AnyIterable;
import com.saicone.types.TypeParser;
import com.saicone.types.parser.ListParser;
import io.github.miniplaceholders.api.MiniPlaceholders;
import io.github.miniplaceholders.api.types.RelationalAudience;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.regex.Pattern;

@ApiStatus.Internal
public class Mini implements TypeParser<Component> {

    private static final Pattern AMPERSAND_PATTERN = Pattern.compile("(?i)(&[0-9A-FK-OR])|(&#[0-9A-F]{6})");
    private static final Pattern SECTION_PATTERN = Pattern.compile("(?i)(§[0-9A-FK-OR])|(§x(§[0-9A-F]){6})");

    public static Mini INSTANCE = new Mini();

    @NotNull
    public static Mini get() {
        return INSTANCE;
    }

    private final MiniMessage miniMessage;
    private final ListParser<Component> listParser;

    private final Lazy<Boolean> useMiniPlaceholders = Lazy.of(() -> {
        try {
            Class.forName("io.github.miniplaceholders.api.MiniPlaceholders");
            return true;
        } catch (Throwable t) {
            return false;
        }
    });

    public Mini() {
        this(MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("small", SmallFont.TAG_SMALL)
                        .build()
                )
                .build());
    }

    public Mini(@NotNull MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
        this.listParser = ListParser.of(this);
    }

    public boolean isUsingMiniPlaceholders() {
        return useMiniPlaceholders.get();
    }

    @Override
    public @NotNull Type getType() {
        return Component.class;
    }

    @NotNull
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @NotNull
    public Audience relational(@NotNull Audience main, @NotNull Audience secondary) {
        if (useMiniPlaceholders.get()) {
            return RelationalAudience.from(main, secondary);
        } else {
            return main;
        }
    }

    @Override
    public @NotNull Component parse(@NotNull Object object) {
        return parse(null, object);
    }

    @NotNull
    public Component parse(@Nullable Pointered target, @NotNull Object object) {
        final Object first = AnyIterable.of(object).first();
        if (first == null) {
            throw new IllegalArgumentException("Cannot parse null object to Component");
        }

        if (first instanceof Component) {
            return (Component) first;
        } else if (first instanceof String str) {
            if (AMPERSAND_PATTERN.matcher(str).find()) {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(str);
            }
            if (SECTION_PATTERN.matcher(str).find()) {
                return LegacyComponentSerializer.legacySection().deserialize(str);
            }
            return deserialize(target, str);
        } else {
            throw new IllegalArgumentException("Cannot parse object of type " + first.getClass().getName() + " to Component");
        }
    }

    @NotNull
    public Component parseOrEmpty(@Nullable Pointered target, @Nullable Object object) {
        if (object == null) {
            return Component.empty();
        }
        return parse(target, object);
    }

    @NotNull
    public Component deserialize(@Nullable Pointered target, @NotNull String str) {
        if (useMiniPlaceholders.get()) {
            if (target != null) {
                if (target instanceof RelationalAudience<?>) {
                    return miniMessage.deserialize(str, target, MiniPlaceholders.relationalGlobalPlaceholders());
                } else {
                    return miniMessage.deserialize(str, target, MiniPlaceholders.audienceGlobalPlaceholders());
                }
            } else {
                return miniMessage.deserialize(str, MiniPlaceholders.globalPlaceholders());
            }
        } else {
            return miniMessage.deserialize(str);
        }
    }

    @Override
    public @NotNull ListParser<Component> list() {
        return listParser;
    }
}
