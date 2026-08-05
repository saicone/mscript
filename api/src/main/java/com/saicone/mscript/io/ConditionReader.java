package com.saicone.mscript.io;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Operator;
import com.saicone.mscript.Value;
import com.saicone.mscript.util.Values;
import com.saicone.types.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class ConditionReader extends StringReader {

    private static final Map<String, Operator> OPERATORS = new HashMap<>();
    private static final Map<Character, Set<Character>> OPERATOR_CHARS = new HashMap<>();

    static {
        OPERATORS.put("+", Operator.Arithmetic.ADD);
        OPERATORS.put("-", Operator.Arithmetic.SUBTRACT);
        OPERATORS.put("*", Operator.Arithmetic.MULTIPLY);
        OPERATORS.put("/", Operator.Arithmetic.DIVIDE);
        OPERATORS.put("%", Operator.Arithmetic.REMAIN);

        OPERATORS.put("==", Operator.Relational.EQUALS);
        OPERATORS.put("!=", Operator.Relational.NOT_EQUALS);
        OPERATORS.put(">", Operator.Relational.GREATER_THAN);
        OPERATORS.put(">=", Operator.Relational.GREATER_OR_EQUALS);
        OPERATORS.put("<", Operator.Relational.LESS_THAN);
        OPERATORS.put("<=", Operator.Relational.LESS_OR_EQUALS);

        OPERATORS.put("&&", Operator.Logical.AND);
        OPERATORS.put("||", Operator.Logical.OR);

        OPERATORS.put("&", Operator.Bitwise.AND);
        OPERATORS.put("|", Operator.Bitwise.OR);
        OPERATORS.put("^", Operator.Bitwise.XOR);

        for (Map.Entry<String, Operator> entry : OPERATORS.entrySet()) {
            final String key = entry.getKey();
            final char first = key.charAt(0);
            if (key.length() > 1) {
                final char second = key.charAt(1);
                OPERATOR_CHARS.computeIfAbsent(first, c -> new HashSet<>()).add(second);
            } else {
                OPERATOR_CHARS.computeIfAbsent(first, c -> new HashSet<>());
            }
        }
    }

    private final String string;

    private int depth = 0;

    public ConditionReader(@NotNull String str) {
        super(str);
        this.string = str;
    }

    @Nullable
    public Condition readCondition() throws IOException {
        final Value<?> operation = readOperation();
        if (operation == null) {
            return null;
        }
        if (this.depth != 0) {
            throw new IOException("Unmatched parentheses in condition");
        }
        return new Condition() {
            @Override
            public @Nullable Boolean test(@NotNull Context context) {
                final Object result = operation.get(context);
                if (Values.isUnknown(result)) {
                    return null;
                }
                return Values.isTrue(result);
            }

            @Override
            public String toString() {
                return string;
            }
        };
    }

    @Nullable
    public Value<?> readOperation() throws IOException {
        Value<?> current = readValue();
        if (current == null) {
            return null;
        }

        while (true) {
            final Operator operator = readOperator();
            if (operator == null) {
                break;
            }

            final int depthBefore = this.depth;
            final Value<?> bValue = operator instanceof Operator.Logical || operator instanceof Operator.Bitwise ? readOperation() : readValue();
            if (bValue == null) {
                throw new IOException("Expected value after operator");
            }

            final Value<?> aValue = current;
            current = t -> operator.eval(t, aValue, bValue);

            if (this.depth > depthBefore) {
                break;
            }
        }

        return current;
    }

    @Nullable
    protected Value<?> readValue() throws IOException {
        skipSpaces();
        final boolean not;
        if (peek() == '!') {
            read();
            not = true;
        } else {
            not = false;
        }

        final Value<?> result = switch (peek()) {
            case '(' -> {
                read();
                this.depth++;
                yield readOperation();
            }
            case '"' -> {
                read();
                yield readValue('"');
            }
            case '`' -> {
                read();
                yield readValue('`');
            }
            case '\'' -> {
                read();
                yield readValue('\'');
            }
            default -> readValue(' ');
        };

        if (result == null) {
            return null;
        }

        if (not) {
            return context -> {
                final Object value = result.get(context);
                if (value == null) {
                    return null;
                }
                // With '!' operator, the result is considered a boolean
                final Boolean bool = Types.BOOLEAN.parseOrDefault(value, null);
                if (bool == null) {
                    return null;
                }
                return !bool;
            };
        } else {
            return result;
        }
    }

    @Nullable
    protected Value<?> readValue(char end) throws IOException {
        final StringBuilder builder = new StringBuilder();

        int peek;
        while ((peek = peek()) != -1) {
            if (peek == '\\') {
                read();
                final int next = peek();
                if (next == end || (this.depth > 0 && next == ')')) {
                    builder.append((char) read());
                    continue;
                }
                builder.append('\\');
                continue;
            }
            if (peek == end || (this.depth > 0 && peek == ')')) {
                break;
            }
            builder.append((char) read());
        }

        if (peek == end) {
            read();
        } else if (peek == ')') {
            read();
            this.depth--;
        }

        if (end == '\'') {
            return Value.literal(builder.toString());
        } else {
            return readValue(builder.toString());
        }
    }

    @Nullable
    protected abstract Value<?> readValue(@NotNull String str);

    @Nullable
    protected Operator readOperator() throws IOException {
        skipSpaces();

        final int peek = peek();
        if (peek == -1) {
            return null;
        }

        final char first = (char) peek;
        final Set<Character> seconds = OPERATOR_CHARS.get(first);
        if (seconds == null) {
            throw new IOException("Invalid operator: '" + first + "'");
        }
        read();

        final int next = peek();
        if (next != -1 && seconds.contains((char) next)) {
            final char second = (char) read();
            final Operator operator = OPERATORS.get("" + first + second);
            if (operator != null) {
                return operator;
            }
            throw new IOException("Invalid operator: " + first + second);
        }

        final Operator single = OPERATORS.get(String.valueOf(first));
        if (single != null) {
            return single;
        }

        if (next == -1) {
            throw new IOException("Invalid operator: '" + first + "'");
        } else {
            throw new IOException("Invalid operator: '" + first + (char) next + "'");
        }
    }

    public int peek() throws IOException {
        mark(1);
        int c = read();
        reset();
        return c;
    }

    @Nullable
    public String peek(int length) throws IOException {
        mark(length);
        char[] chars = new char[length];
        int read = read(chars);
        reset();
        if (read == length) {
            return new String(chars);
        }
        return null;
    }

    public int skipSpaces() throws IOException {
        int count = 0;
        while (peek() == ' ' || peek() == '\t' || peek() == '\n' || peek() == '\r') {
            read();
            count++;
        }
        return count;
    }
}
