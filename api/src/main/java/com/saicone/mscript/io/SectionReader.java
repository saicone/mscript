package com.saicone.mscript.io;

import com.saicone.mscript.Section;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SectionReader<T extends Section> {

    @NotNull
    @ApiStatus.Internal
    public static <T extends Section> SectionReader<T> unary(@NotNull @Language("RegExp") String regex, @NotNull Function<String, T> constructor) {
        return new SectionReader<>(regex) {
            @Override
            protected T read(@NotNull String id) {
                return constructor.apply(id);
            }

            @Override
            public T read(@NotNull String id, @Nullable Object context) {
                return read(id);
            }
        };
    }

    private final @Language("RegExp") String regex;
    private final Pattern pattern;
    private final String defaultKey;

    public SectionReader(@NotNull @Language("RegExp") String regex) {
        this(regex.startsWith("(?i)") ? regex : "(?i)" + regex, "value");
    }

    public SectionReader(@NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.defaultKey = defaultKey;
    }

    @NotNull
    public String regex() {
        return regex;
    }

    @NotNull
    public Pattern pattern() {
        return pattern;
    }

    @NotNull
    public String defaultKey() {
        return defaultKey;
    }

    @UnknownNullability
    protected T read(@NotNull String id) {
        throw new IllegalStateException("[" + id + "] Unsupported type: void");
    }

    @UnknownNullability
    public T read(@NotNull String id, @Nullable Object context) {
        if (context == null) {
            return read(id);
        } else if (context instanceof Boolean) {
            return read(id, (Boolean) context);
        } else if (context instanceof Number) {
            return read(id, (Number) context);
        } else if (context instanceof String) {
            return read(id, (String) context);
        } else if (context instanceof Map<?, ?>) {
            return read(id, (Map<?, ?>) context);
        } else if (context instanceof List<?>) {
            return read(id, (List<?>) context);
        } else {
            throw new IllegalStateException("[" + id + "] Unsupported type: " + context.getClass().getName());
        }
    }

    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Boolean context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: Boolean");
    }

    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Number context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: Number");
    }

    @UnknownNullability
    protected T read(@NotNull String id, @NotNull String context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: String");
    }

    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Map<?, ?> context) {
        return read(id, context.get(this.defaultKey));
    }

    @UnknownNullability
    protected T read(@NotNull String id, @NotNull List<?> context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: List");
    }

    @Override
    public final boolean equals(Object object) {
        if (!(object instanceof SectionReader<?> that)) return false;

        return regex.equals(that.regex) && defaultKey.equals(that.defaultKey);
    }

    @Override
    public int hashCode() {
        int result = regex.hashCode();
        result = 31 * result + defaultKey.hashCode();
        return result;
    }
}
