package com.saicone.mscript;

import com.saicone.mscript.test.TestContext;
import com.saicone.mscript.test.TestSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OperatorTest {

    private final TestContext context = new TestContext(TestSender.player("TestPlayer"), null);

    @Test
    void testArithmeticOperators() {
        // Addition
        assertEquals((long) 1234 + 1234, eval(Operator.Arithmetic.ADD, "{test_integer}", "{test_integer}"));
        assertEquals("STRINGSTRING", eval(Operator.Arithmetic.ADD, "{test_string1}", "{test_string1}"));
        assertEquals((double) 1234.56 + 10.0, eval(Operator.Arithmetic.SUBTRACT, "1254.56", "10.0"));

        // Subtraction
        assertEquals((long) 1234 - 100, eval(Operator.Arithmetic.SUBTRACT, "{test_integer}", "100"));

        // Multiplication
        assertEquals((long) 1234 * 2, eval(Operator.Arithmetic.MULTIPLY, "{test_integer}", "2"));

        // Division
        assertEquals((long) 1234 / 2, eval(Operator.Arithmetic.DIVIDE, "{test_integer}", "2"));
        assertThrows(ArithmeticException.class, () ->
            eval(Operator.Arithmetic.DIVIDE, "10", "0"));

        // Remainder
        assertEquals((long) 1234 % 10, eval(Operator.Arithmetic.REMAIN, "{test_integer}", "10"));
    }

    @Test
    void testRelationalOperators() {
        // Equals
        assertTrue((Boolean) eval(Operator.Relational.EQUALS, "{test_integer}", "1234"));
        assertFalse((Boolean) eval(Operator.Relational.EQUALS, "{test_integer}", "5678"));

        // Not equals
        assertTrue((Boolean) eval(Operator.Relational.NOT_EQUALS, "{test_integer}", "5678"));
        assertFalse((Boolean) eval(Operator.Relational.NOT_EQUALS, "{test_integer}", "1234"));

        // Greater than
        assertTrue((Boolean) eval(Operator.Relational.GREATER_THAN, "{test_decimal}", "1000"));
        assertFalse((Boolean) eval(Operator.Relational.GREATER_THAN, "{test_integer}", "5000"));

        // Greater or equals
        assertTrue((Boolean) eval(Operator.Relational.GREATER_OR_EQUALS, "{test_integer}", "1234"));
        assertTrue((Boolean) eval(Operator.Relational.GREATER_OR_EQUALS, "{test_integer}", "1000"));

        // Less than
        assertTrue((Boolean) eval(Operator.Relational.LESS_THAN, "{test_integer}", "5000"));
        assertFalse((Boolean) eval(Operator.Relational.LESS_THAN, "{test_integer}", "1000"));

        // Less or equals
        assertTrue((Boolean) eval(Operator.Relational.LESS_OR_EQUALS, "{test_integer}", "1234"));
        assertTrue((Boolean) eval(Operator.Relational.LESS_OR_EQUALS, "{test_integer}", "5000"));
    }

    @Test
    void testLogicalOperators() {
        // AND
        assertTrue((Boolean) eval(Operator.Logical.AND, "{test_boolean_true}", "{test_boolean_yes}"));
        assertFalse((Boolean) eval(Operator.Logical.AND, "{test_boolean_true}", "{test_boolean_false}"));
        assertFalse((Boolean) eval(Operator.Logical.AND, "{test_boolean_false}", "{test_boolean_no}"));

        // OR
        assertTrue((Boolean) eval(Operator.Logical.OR, "{test_boolean_true}", "{test_boolean_false}"));
        assertTrue((Boolean) eval(Operator.Logical.OR, "{test_boolean_yes}", "{test_boolean_no}"));
        assertFalse((Boolean) eval(Operator.Logical.OR, "{test_boolean_false}", "{test_boolean_no}"));
    }

    @Test
    void testBitwiseOperators() {
        // AND
        assertEquals((long) 12 & 10, eval(Operator.Bitwise.AND, "12", "10"));

        // OR
        assertEquals((long) 12 | 10, eval(Operator.Bitwise.OR, "12", "10"));

        // XOR
        assertEquals((long) 12 ^ 10, eval(Operator.Bitwise.XOR, "12", "10"));

        // Boolean as bitwise
        assertTrue((Boolean) eval(Operator.Bitwise.AND, "true", "true"));
        assertFalse((Boolean) eval(Operator.Bitwise.AND, "true", "false"));
    }

    private Object eval(Operator operator, String a, String b) {
        return operator.eval(context, Value.using(a), Value.using(b));
    }
}
