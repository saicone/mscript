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

public class SectionCompiler<T extends Section> {

    protected final Set<SectionReader<?>> readers;

    public SectionCompiler() {
        this(new LinkedHashSet<>());
    }

    public SectionCompiler(@NotNull Set<SectionReader<?>> readers) {
        this.readers = readers;
    }

    @NotNull
    public Set<SectionReader<?>> getReaders() {
        return readers;
    }

    public boolean put(@NotNull SectionReader<?> reader) {
        return readers.add(reader);
    }

    public void putAll(@NotNull Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && SectionReader.class.isAssignableFrom(field.getType())) {
                try {
                    field.setAccessible(true);
                    final Object value = field.get(null);
                    if (value instanceof SectionReader<?>) {
                        put((SectionReader<?>) value);
                    }
                } catch (Throwable ignored) { }
            }
        }
    }

    public boolean remove(@NotNull SectionReader<?> reader) {
        return readers.remove(reader);
    }

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

    @NotNull
    public SectionReader<T> compile(@NotNull @Language("RegExp") String regex, @NotNull T t) {
        final SectionReader<T> reader = reader(regex, (id, context) -> t);
        put(reader);
        return reader;
    }

    @NotNull
    public SectionReader<T> compile(@NotNull @Language("RegExp") String regex, @NotNull Function<String, T> function) {
        final SectionReader<T> reader = reader(regex, (id, context) -> function.apply(id));
        put(reader);
        return reader;
    }

    @NotNull
    public SectionReader<T> compileAny(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Object, T> function) {
        final SectionReader<T> reader = reader(regex, function);
        put(reader);
        return reader;
    }

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

    @NotNull
    protected SectionReader<T> reader(@NotNull @Language("RegExp") String regex, @NotNull CompilerFunction<Object, T> function) {
        return new SectionReader<T>(regex) {
            @Override
            public T read(@NotNull String id, @Nullable Object context) {
                return function.apply(id, context);
            }
        };
    }

    public interface CompilerFunction<C, T extends Section> extends BiFunction<String, C, T> {

        @Override
        @Nullable T apply(@NotNull String id, C context);
    }
}
