package com.saicone.mscript.impl.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Value;
import com.saicone.mscript.io.SectionReader;
import com.saicone.types.TypeParser;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ChanceCondition implements Condition {

    public static final TypeParser<Double> PARSER = object -> {
        if (object instanceof Number number) {
            if (number instanceof BigDecimal || number instanceof Double || number instanceof Float) {
                return number.doubleValue();
            } else {
                return number.intValue() / 100.0;
            }
        } else if (object instanceof String str) {
            if (str.endsWith("%")) {
                str = str.substring(0, str.length() - 1);
            }
            return Types.DOUBLE.parse(str) / 100.0;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + object.getClass().getName());
        }
    };

    public static final SectionReader<ChanceCondition> READER = new SectionReader<>("chance|prob(ability)?") {
        @Override
        protected ChanceCondition read(@NotNull String id, @NotNull Number context) {
            return new ChanceCondition(Value.of(PARSER.parse(context)));
        }

        @Override
        protected ChanceCondition read(@NotNull String id, @NotNull String context) {
            return new ChanceCondition(Value.of(PARSER, context));
        }
    };

    private final Value<Double> chance;

    public ChanceCondition(@NotNull Value<Double> chance) {
        this.chance = chance;
    }

    @Override
    public Boolean test(@NotNull Context context) {
        return Math.random() < chance.get(context);
    }
}
