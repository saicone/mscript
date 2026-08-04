package com.saicone.mscript.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Value;
import com.saicone.mscript.impl.ComposedExecution;
import com.saicone.mscript.impl.Conditions;
import com.saicone.mscript.impl.Executions;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ScriptReader {

    public static Pattern IF_PATTERN = Pattern.compile("if|(meet-?)?conditions?", Pattern.CASE_INSENSITIVE);
    public static Pattern RUN_PATTERN = Pattern.compile("run|execut(e|ions?)|actions?", Pattern.CASE_INSENSITIVE);
    public static Pattern ELSE_PATTERN = Pattern.compile("else(-?(run|execut(e|ions?)|actions?))?|deny", Pattern.CASE_INSENSITIVE);

    protected final SectionCompiler<Condition> conditions;
    protected final SectionCompiler<Execution> executions;

    public ScriptReader() {
        this(new SectionCompiler<>(), new SectionCompiler<>());
    }

    public ScriptReader(@NotNull SectionCompiler<Condition> conditions, @NotNull SectionCompiler<Execution> executions) {
        this.conditions = conditions;
        this.executions = executions;

        this.conditions.putAll(Conditions.class);
        this.executions.putAll(Executions.class);
    }

    @NotNull
    public SectionCompiler<Condition> conditions() {
        return conditions;
    }

    @NotNull
    public SectionCompiler<Execution> executions() {
        return executions;
    }

    protected boolean isParseable(@NotNull String str) {
        return true;
    }

    @NotNull
    public Value<?> readValue(@NotNull String s) {
        if (s.equalsIgnoreCase("true")) {
            return Value.literal(true);
        }
        if (s.equalsIgnoreCase("false")) {
            return Value.literal(false);
        }
        if (isParseable(s)) {
            return context -> context.parse(s);
        }
        return Value.literal(s);
    }

    @Nullable
    public Condition readCondition(@Nullable Object object) throws IOException {
        if (object instanceof List<?>) {
            return readCondition((List<?>) object);
        } else if (object instanceof Map<?, ?>) {
            return readCondition((Map<?, ?>) object);
        } else if (object instanceof String) {
            return readCondition((String) object);
        } else if (object != null) {
            final Boolean bool = Types.BOOLEAN.parseOrDefault(object, null);
            if (bool != null) {
                return context -> bool;
            }
        }
        return null;
    }

    @Nullable
    public Condition readCondition(@NotNull List<?> list) throws IOException {
        final List<Condition> conditions = new ArrayList<>();

        for (Object element : list) {
            final Condition condition = readCondition(element);
            if (condition != null) {
                conditions.add(condition);
            }
        }

        if (conditions.isEmpty()) {
            return null;
        }

        if (conditions.size() == 1) {
            return conditions.get(0);
        }

        return context -> {
            Boolean result = null;
            for (Condition condition : conditions) {
                final Boolean bool = condition.test(context);
                if (Boolean.FALSE.equals(bool)) {
                    return false;
                } else if (Boolean.TRUE.equals(bool)) {
                    result = true;
                }
            }
            return result;
        };
    }

    @Nullable
    public Condition readCondition(@NotNull Map<?, ?> map) {
        final List<Condition> conditions = new ArrayList<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();
            final Condition condition = this.conditions.read(key, value);
            if (condition != null) {
                conditions.add(condition);
            }
        }

        if (conditions.isEmpty()) {
            return null;
        }

        if (conditions.size() == 1) {
            return conditions.get(0);
        }

        return context -> {
            Boolean result = null;
            for (Condition condition : conditions) {
                final Boolean bool = condition.test(context);
                if (Boolean.TRUE.equals(bool)) {
                    return true;
                } else if (Boolean.FALSE.equals(bool)) {
                    result = false;
                }
            }
            return result;
        };
    }

    @Nullable
    public Condition readCondition(@NotNull String s) throws IOException {
        final ConditionReader reader = new ConditionReader(s) {
            @Override
            protected @NotNull Value<?> readValue(@NotNull String str) {
                return ScriptReader.this.readValue(str);
            }
        };

        try {
            return reader.readCondition();
        } finally {
            reader.close();
        }
    }

    @Nullable
    public Execution readExecution(@Nullable Object object) throws IOException {
        if (object instanceof List<?>) {
            return readExecution((List<?>) object);
        } else if (object instanceof Map<?, ?>) {
            return readExecution((Map<?, ?>) object);
        } else if (object instanceof String) {
            return readExecution((String) object);
        } else {
            return null;
        }
    }

    @Nullable
    public Execution readExecution(@NotNull List<?> list) throws IOException {
        final List<Execution> executions = readExecution0(list);

        if (executions.isEmpty()) {
            return null;
        }

        if (executions.size() == 1) {
            return executions.get(0);
        }

        return new ComposedExecution(null, executions, List.of());
    }

    @NotNull
    private List<Execution> readExecution0(@NotNull List<?> list) throws IOException {
        final List<Execution> executions = new ArrayList<>();

        for (Object element : list) {
            final Execution execution = readExecution(element);
            if (execution != null) {
                executions.add(execution);
            }
        }

        return executions;
    }

    @Nullable
    public Execution readExecution(@NotNull Map<?, ?> map) throws IOException {
        Condition condition = null;
        final List<Execution> ifExecution = new ArrayList<>();
        final List<Execution> elseExecution = new ArrayList<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Object value = entry.getValue();

            if (IF_PATTERN.matcher(key).matches()) {
                final Condition readCondition = readCondition(value);
                if (readCondition != null) {
                    condition = readCondition;
                }
            } else if (RUN_PATTERN.matcher(key).matches()) {
                if (value instanceof List<?>) {
                    ifExecution.addAll(readExecution0((List<?>) value));
                } else {
                    final Execution execution = this.executions.read(key, value);
                    if (execution != null) {
                        ifExecution.add(execution);
                    }
                }
            } else if (ELSE_PATTERN.matcher(key).matches()) {
                if (value instanceof List<?>) {
                    elseExecution.addAll(readExecution0((List<?>) value));
                } else {
                    final Execution execution = this.executions.read(key, value);
                    if (execution != null) {
                        elseExecution.add(execution);
                    }
                }
            } else {
                final Execution execution = this.executions.read(key, value);
                if (execution != null) {
                    ifExecution.add(execution);
                }
            }
        }

        if (condition == null && ifExecution.isEmpty() && elseExecution.isEmpty()) {
            return null;
        }

        return new ComposedExecution(condition, ifExecution, elseExecution);
    }

    @Nullable
    public Execution readExecution(@NotNull String s) {
        if (s.isBlank()) {
            return null;
        }

        final String id;
        String context;
        if (s.charAt(0) == '[') {
            final int index = s.indexOf(']');
            if (index <= 0) {
                return null;
            }
            id = s.substring(1, index).trim();
            context = s.substring(index + 1);
        } else {
            final int index1 = s.indexOf(": ");
            final int index2 = s.indexOf('=');
            final int separator = index1 >= 0 && index2 >= 0 ? Math.min(index1, index2) : Math.max(index1, index2);
            if (separator > 0) {
                id = s.substring(0, separator).trim();
                context = s.substring(separator + 1);
            } else {
                id = s.trim();
                context = null;
            }
        }

        if (context != null && context.startsWith(" ")) {
            context = context.substring(1);
        }

        return this.executions.read(id, context);
    }
}
