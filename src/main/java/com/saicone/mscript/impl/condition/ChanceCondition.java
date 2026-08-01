package com.saicone.mscript.impl.condition;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class ChanceCondition extends SingleSection<Double> implements Condition {

    public static final SectionReader<ChanceCondition> READER = reader("chance|prob(ability)?", ChanceCondition::new);

    public ChanceCondition(@Nullable Object object) {
        super(object);
    }

    @Override
    protected @NotNull Double parse(@NotNull Object object) {
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
    }

    @Override
    public Boolean test(@NotNull Context context) {
        return Math.random() < getValue(context);
    }
}
