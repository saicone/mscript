package com.saicone.mscript.io;

import com.saicone.mscript.Section;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SectionReader<T extends Section> {

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

    protected T read(@NotNull String id) {
        throw new IllegalStateException("Unsupported type: void");
    }

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
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        }
    }

    protected T read(@NotNull String id, @NotNull Boolean context) {
        throw new IllegalStateException("Unsupported type: Boolean");
    }

    protected T read(@NotNull String id, @NotNull Number context) {
        throw new IllegalStateException("Unsupported type: Number");
    }

    protected T read(@NotNull String id, @NotNull String context) {
        throw new IllegalStateException("Unsupported type: String");
    }

    protected T read(@NotNull String id, @NotNull Map<?, ?> context) {
        return read(id, context.get(this.defaultKey));
    }

    protected T read(@NotNull String id, @NotNull List<?> context) {
        throw new IllegalStateException("Unsupported type: List");
    }
}
