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

/**
 * Class that reads a section from a given identifier an object context.<br>
 * This is like a key and value converter.
 *
 * @author Rubenicos
 *
 * @param <T> the type of section to read
 */
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

    /**
     * Constructs a new SectionReader with the given regex and default key {@code "value"}.
     *
     * @param regex the regex pattern to match identifiers
     */
    public SectionReader(@NotNull @Language("RegExp") String regex) {
        this(regex.startsWith("(?i)") ? regex : "(?i)" + regex, "value");
    }

    /**
     * Constructs a new SectionReader with the given regex and default key.
     *
     * @param regex      the regex pattern to match identifiers
     * @param defaultKey the default key to use when reading from a map
     */
    public SectionReader(@NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
        this.defaultKey = defaultKey;
    }

    /**
     * Get the regex pattern used by this SectionReader.
     *
     * @return a regex pattern
     */
    @NotNull
    public String regex() {
        return regex;
    }

    /**
     * Get the compiled regex pattern used by this SectionReader.
     *
     * @return a compiled regex pattern
     */
    @NotNull
    public Pattern pattern() {
        return pattern;
    }

    /**
     * Get the default key used by this SectionReader when reading from a map.
     *
     * @return a string key
     */
    @NotNull
    public String defaultKey() {
        return defaultKey;
    }

    /**
     * Read a section with the given identifier and {@code void} value.
     *
     * @param id the identifier of the section
     * @return   the section
     */
    @UnknownNullability
    protected T read(@NotNull String id) {
        throw new IllegalStateException("[" + id + "] Unsupported type: void");
    }

    /**
     * Read a section with the given identifier and context.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
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

    /**
     * Read a section with the given identifier and {@link Boolean} context.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Boolean context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: Boolean");
    }

    /**
     * Read a section with the given identifier and {@link Number} context.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Number context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: Number");
    }

    /**
     * Read a section with the given identifier and {@link String} context.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    @UnknownNullability
    protected T read(@NotNull String id, @NotNull String context) {
        throw new IllegalStateException("[" + id + "] Unsupported type: String");
    }

    /**
     * Read a section with the given identifier and {@link Map} context.<br>
     * This method by default subtracts the value of the default key from the map and calls {@link #read(String, Object)} with it.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
    @UnknownNullability
    protected T read(@NotNull String id, @NotNull Map<?, ?> context) {
        return read(id, context.get(this.defaultKey));
    }

    /**
     * Read a section with the given identifier and {@link List} context.
     *
     * @param id      the identifier of the section
     * @param context the context to read from
     * @return        the section
     */
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
