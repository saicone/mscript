package com.saicone.mscript.util;

import com.saicone.types.AnyIterable;
import com.saicone.types.TypeParser;
import com.saicone.types.parser.ListParser;
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
public final class MTypes {

    public static TypeParser<Component> COMPONENT = new TypeParser<>() {
        private final MiniMessage minimessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .tag("small", SmallFont.TAG_SMALL)
                        .build()
                )
                .build();
        private final Pattern AMPERSAND_PATTERN = Pattern.compile("(?i)(&[0-9A-FK-OR])|(&#[0-9A-F]{6})");
        private final Pattern SECTION_PATTERN = Pattern.compile("(?i)(§[0-9A-FK-OR])|(§x(§[0-9A-F]){6})");

        @Override
        public @NotNull Type getType() {
            return Component.class;
        }

        @Override
        public @Nullable Component parse(@NotNull Object object) {
            final Object first = AnyIterable.of(object).first();
            if (first == null) {
                return null;
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
                return minimessage.deserialize(str);
            } else {
                return null;
            }
        }
    };
    public static ListParser<Component> COMPONENT_LIST = COMPONENT.list();

    MTypes() {
    }
}
