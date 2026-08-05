package com.saicone.mscript;

import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.test.TestContext;
import com.saicone.mscript.test.TestSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionTest {

    private ScriptReader reader;
    private TestContext context;

    @BeforeEach
    void setUp() {
        reader = new ScriptReader();
        context = new TestContext(TestSender.player("TestPlayer"), null);
    }

    @Test
    void testSimpleConditions() throws IOException {
        assertTrue(reader.readCondition("30 > 10").test(context));
        assertFalse(reader.readCondition("10 > 30").test(context));
        assertTrue(reader.readCondition("10 == 10").test(context));
        assertFalse(reader.readCondition("10 != 10").test(context));
        assertTrue(reader.readCondition("10 <= 10").test(context));
        assertTrue(reader.readCondition("10 >= 10").test(context));
    }

    @Test
    void testConditionsWithReplacements() throws IOException {
        assertTrue(reader.readCondition("\"{test_integer}\" == \"1234\"").test(context));
        assertTrue(reader.readCondition("`{test_integer}` > `1000`").test(context));
        assertTrue(reader.readCondition("\"{test_decimal}\" > \"1000\"").test(context));
        assertTrue(reader.readCondition("\"{test_boolean_true}\" == \"true\"").test(context));
    }

    @Test
    void testLiteralStrings() throws IOException {
        assertFalse(reader.readCondition("'test' == 'other'").test(context));
        assertTrue(reader.readCondition("'test' == 'test'").test(context));
        assertFalse(reader.readCondition("\"{test_string2}\" == '{test_string2}'").test(context));
        assertTrue(reader.readCondition("`{test_string3}` == '{test_string3}'").test(context));
    }

    @Test
    void testLogicalConditions() throws IOException {
        assertTrue(reader.readCondition("30 > 10 && 20 > 10").test(context));
        assertFalse(reader.readCondition("30 > 10 && 10 > 20").test(context));
        assertTrue(reader.readCondition("30 > 10 || 10 > 20").test(context));
        assertFalse(reader.readCondition("10 > 30 || 5 > 20").test(context));
    }

    @Test
    void testBuiltInConditions() throws IOException {
        assertTrue(reader.readCondition("player").test(context));
        assertFalse(reader.readCondition("console").test(context));

        TestContext serverContext = new TestContext(TestSender.server(), null);
        assertFalse(reader.readCondition("player").test(serverContext));
        assertTrue(reader.readCondition("console").test(serverContext));
    }
}
