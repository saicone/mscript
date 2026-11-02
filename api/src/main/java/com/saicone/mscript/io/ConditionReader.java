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

/**
 * An abstract string reader to read conditions in a similar way to how Java evaluates expressions.
 * It supports arithmetic, relational, logical and bitwise operators.<br>
 * The only method that needs to be implemented is {@link #readValue(String)} which is responsible for reading a {@link Value} from a string.
 *
 * @author Rubenicos
 */
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

    /**
     * Constructs a new ConditionReader with the given string.
     *
     * @param str the string to read conditions from
     */
    public ConditionReader(@NotNull String str) {
        super(str);
        this.string = str;
    }

    /**
     * Reads a condition from the string.
     *
     * @return the condition read, or null if no condition was found
     * @throws IOException if an I/O error occurs
     */
    @Nullable
    public Condition readCondition() throws IOException {
        final Value<?> operation = readOperation();
        if (operation == null) {
            return null;
        }
        if (this.depth != 0) {
            throw new IOException("Unmatched parentheses in condition, depth: " + this.depth);
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

    /**
     * Reads an operation from the string.
     *
     * @return the operation read, or null if no operation was found
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Reads a value from the string.
     *
     * @return the value read, or null if no value was found
     * @throws IOException if an I/O error occurs
     */
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
                final Value<?> value = readOperation();
                skipSpaces();
                if (peek() == ')') {
                    read();
                    this.depth--;
                } else {
                    throw new IOException("Expected ')' to close parenthesis");
                }
                yield value;
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

    /**
     * Reads a value from the string until the specified end character is encountered.
     *
     * @param end the character that signifies the end of the block to read
     * @return    the value read, or null if no value was found
     * @throws IOException if an I/O error occurs
     */
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

        if (peek == end && (end == '"' || end == '`' || end == '\'')) {
            read();
        }

        if (end == '\'') {
            return Value.literal(builder.toString());
        } else {
            return readValue(builder.toString());
        }
    }

    /**
     * Reads a value from the string.
     *
     * @param str the string to read the value from
     * @return    the value read, or null if no value was found
     */
    @Nullable
    protected abstract Value<?> readValue(@NotNull String str);

    /**
     * Reads an operator from the string.
     *
     * @return the operator read, or null if no operator was found
     * @throws IOException if an I/O error occurs
     */
    @Nullable
    protected Operator readOperator() throws IOException {
        skipSpaces();

        final int peek = peek();
        if (peek == -1) {
            return null;
        }

        final char first = (char) peek;
        if (first == ')') {
            return null;
        }
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

    /**
     * Peeks the next character in the string without consuming it.
     *
     * @return the next character, or -1 if the end of the string has been reached
     * @throws IOException if an I/O error occurs
     */
    public int peek() throws IOException {
        mark(1);
        int c = read();
        reset();
        return c;
    }

    /**
     * Peeks the next characters in the string without consuming them.
     *
     * @param length the number of characters to peek
     * @return       the next characters as a string, or null if the end of the string has been reached
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Skips whitespace characters in the string.
     *
     * @return the number of whitespace characters skipped
     * @throws IOException if an I/O error occurs
     */
    public int skipSpaces() throws IOException {
        int count = 0;
        while (peek() == ' ' || peek() == '\t' || peek() == '\n' || peek() == '\r') {
            read();
            count++;
        }
        return count;
    }
}
