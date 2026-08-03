package com.saicone.mscript.util;

import com.saicone.types.Types;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

@ApiStatus.Internal
public final class Values {

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        // This headless conversion is made to compare non-declared object types
        // For example, a Boolean with a "true" String, or a Number with a "1" String
        if (a instanceof Boolean || b instanceof Boolean) {
            // A null object is considered false when compared to a Boolean, so we can safely default to false
            final Boolean aBool = Types.BOOLEAN.parseOrDefault(a, false);
            final Boolean bBool = Types.BOOLEAN.parseOrDefault(b, false);
            return aBool == bBool;
        } else if (a instanceof Number || b instanceof Number) {
            final BigDecimal aNum = Types.BIG_DECIMAL.parseOrDefault(a, null);
            final BigDecimal bNum = Types.BIG_DECIMAL.parseOrDefault(b, null);
            return aNum != null && bNum != null && aNum.compareTo(bNum) == 0;
        } else {
            return Objects.equals(a, b);
        }
    }

    public static boolean notEquals(@Nullable Object a, @Nullable Object b) {
        return !equals(a, b);
    }

    @ApiStatus.Internal
    public static boolean isUnknown(@Nullable Object object) {
        return object == null || "null".equals(object) || "unknown".equals(object) || "undefined".equals(object);
    }

    @ApiStatus.Internal
    public static boolean isValid(@Nullable Object object) {
        return !isUnknown(object);
    }

    @ApiStatus.Internal
    public static boolean areUnknown(@Nullable Object a, @Nullable Object b) {
        return isUnknown(a) && isUnknown(b);
    }

    @ApiStatus.Internal
    public static boolean areValid(@Nullable Object a, @Nullable Object b) {
        return isValid(a) && isValid(b);
    }

    @ApiStatus.Internal
    public static boolean isTrue(@NotNull Object object) {
        return Boolean.TRUE.equals(Types.BOOLEAN.parseOrDefault(object, null));
    }

    @ApiStatus.Internal
    public static boolean isFalse(@NotNull Object object) {
        return Boolean.FALSE.equals(Types.BOOLEAN.parseOrDefault(object, null));
    }

    Values() {
    }
}
