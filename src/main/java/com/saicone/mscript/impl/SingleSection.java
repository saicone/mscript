package com.saicone.mscript.impl;

import com.saicone.mscript.Context;
import com.saicone.mscript.Section;
import com.saicone.mscript.Value;
import com.saicone.mscript.io.SectionReader;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Function;

@ApiStatus.Internal
public abstract class SingleSection<T> implements Section {

    @NotNull
    protected static <T extends SingleSection<?>> SectionReader<T> reader(@NotNull @Language("RegExp") String regex, @NotNull Function<Object, T> constructor) {
        return new SectionReader<>(regex) {
            @Override
            public T read(@NotNull String id, @Nullable Object context) {
                return constructor.apply(context);
            }
        };
    }

    private final Value<T> value;

    protected SingleSection(@Nullable Object object) {
        this.value = read(object);
    }

    protected SingleSection(@NotNull Value<T> value) {
        this.value = value;
    }

    @NotNull
    protected Value<T> read(@Nullable Object object) {
        if (object instanceof String str) {
            return Value.using(this::parse, str);
        } else if (object != null) {
            return Value.literal(parse(object));
        } else {
            return Value.literal(null);
        }
    }

    @NotNull
    protected abstract T parse(@NotNull Object object);

    @NotNull
    public Value<T> getValue() {
        return value;
    }

    @UnknownNullability
    public T getValue(@NotNull Context context) {
        return value.get(context);
    }
}
