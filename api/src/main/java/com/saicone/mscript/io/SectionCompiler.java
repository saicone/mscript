package com.saicone.mscript.io;

import com.saicone.mscript.Section;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class that stores multiple {@link SectionReader} to match a given identifier and object context
 * to pass to the {@link SectionReader#read(String, Object)} method.
 *
 * @author Rubenicos
 *
 * @param <T> the type of section to read
 */
public class SectionCompiler<T extends Section> {

    protected final Set<SectionReader<?>> readers;

    /**
     * Constructs a new SectionCompiler with an empty set of readers.
     */
    public SectionCompiler() {
        this(new LinkedHashSet<>());
    }

    /**
     * Constructs a new SectionCompiler with the given set of readers.
     *
     * @param readers the set of readers to use
     */
    public SectionCompiler(@NotNull Set<SectionReader<?>> readers) {
        this.readers = readers;
    }

    /**
     * Get the set of readers in this compiler.
     *
     * @return the set of readers
     */
    @NotNull
    public Set<SectionReader<?>> getReaders() {
        return readers;
    }

    /**
     * Add a reader to this compiler.
     *
     * @param reader the reader to add
     * @return       true if the reader was added, false if it was already present
     */
    public boolean put(@NotNull SectionReader<?> reader) {
        readers.remove(reader);
        return readers.add(reader);
    }

    /**
     * Add all readers from the given class to this compiler.
     *
     * @param clazz the class containing static SectionReader fields
     */
    public void putAll(@NotNull Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && SectionReader.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(null);
                    if (value instanceof Supplier<?> supplier) {
                        value = supplier.get();
                    }
                    if (value instanceof SectionReader<?>) {
                        put((SectionReader<?>) value);
                    }
                } catch (Throwable ignored) { }
            }
        }
    }

    /**
     * Remove a reader from this compiler.
     *
     * @param reader the reader to remove
     * @return       true if the reader was removed, false if it was not present
     */
    public boolean remove(@NotNull SectionReader<?> reader) {
        return readers.remove(reader);
    }

    /**
     * Read a section from this compiler if the given identifier matches any of the readers.
     *
     * @param id      the identifier of the section
     * @param context the context object
     * @return        the section, or null if no reader matches
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public T read(@NotNull String id, @Nullable Object context) {
        for (SectionReader<?> reader : this.readers) {
            if (reader.pattern().matcher(id).matches()) {
                return (T) reader.read(id, context);
            }
        }
        return null;
    }

    /**
     * Create and append a section reader that always returns the given value.
     *
     * @param regex the regular expression to match section identifiers
     * @param t     the value to return
     * @return      a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compile(@NotNull @Language("RegExp") String regex, @NotNull T t) {
        final SectionReader<T> reader = reader(regex, (id, context) -> t);
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compile(@NotNull @Language("RegExp") String regex, @NotNull Function<String, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> function.apply(id));
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileAny(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Object, T> function) {
        final SectionReader<T> reader = reader(regex, function);
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link Boolean} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileBoolean(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Boolean, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> {
            if (context instanceof Boolean) {
                return function.apply(id, (Boolean) context);
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link Number} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileNumber(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Number, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> {
            if (context instanceof Number) {
                return function.apply(id, (Number) context);
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link String} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileString(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<String, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> {
            if (context instanceof String) {
                return function.apply(id, (String) context);
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link Map} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileMap(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Map<?, ?>, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> {
            if (context instanceof Map<?, ?>) {
                return function.apply(id, (Map<?, ?>) context);
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    /**
     * Create and append a section reader that applies the given function to the section identifier and a {@link List} context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    public SectionReader<T> compileList(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<List<?>, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> {
            if (context instanceof List<?>) {
                return function.apply(id, (List<?>) context);
            }
            throw new IllegalStateException("Unsupported type: " + context.getClass().getName());
        });
        put(reader);
        return reader;
    }

    /**
     * Create a section reader that applies the given function to the section identifier and context.
     *
     * @param regex    the regular expression to match section identifiers
     * @param function the function to apply to the section identifier and context
     * @return         a newly generated section reader
     */
    @NotNull
    protected SectionReader<T> reader(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Object, T> function) {
        return new SectionReader<T>(regex) {
            @Override
            public T read(@NotNull String id, @Nullable Object context) {
                return function.apply(id, context);
            }
        };
    }

    /**
     * A function that takes a section identifier and a context object and returns a section.
     *
     * @param <C> the type of the context object
     * @param <T> the type of section to return
     */
    public interface CompilerFunction<C, T extends Section> extends BiFunction<String, C, T> {

        @Override
        @Nullable T apply(@NotNull String id, C context);
    }
}
