package com.saicone.mscript;

import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.test.TestContext;
import com.saicone.mscript.test.TestSender;
import com.saicone.mscript.test.io.TestScriptReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionTest {

    private ScriptReader reader;
    private TestContext context;

    @BeforeEach
    void setUp() {
        reader = new TestScriptReader();
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

    @Test
    void testConditionsWithParentheses() throws IOException {
        assertTrue(reader.readCondition("(30 > 10) && (20 > 10)").test(context));
        assertTrue(reader.readCondition("(30 > 10) || (10 > 20)").test(context));
        assertFalse(reader.readCondition("(10 > 30) && (20 > 10)").test(context));
        assertTrue(reader.readCondition("((30 > 10) && (20 > 10)) || (10 > 20)").test(context));
        assertFalse(reader.readCondition("(10 > 30) && ((20 > 10) || (5 > 10))").test(context));
    }

    @Test
    void testReplacementsWithoutQuotes() throws IOException {
        assertTrue(reader.readCondition("{test_integer} > 1000").test(context));
        assertTrue(reader.readCondition("{test_integer} == 1234").test(context));
        assertTrue(reader.readCondition("{test_integer2} > {test_integer3}").test(context));
        assertFalse(reader.readCondition("{test_integer3} > {test_integer2}").test(context));
    }

    @Test
    void testListConditionsAsAnd() throws IOException {
        List<Object> conditions = List.of(
                "30 > 10",
                "20 > 10",
                "100 > 50"
        );
        assertTrue(reader.readCondition(conditions).test(context));

        conditions = List.of(
                "30 > 10",
                "10 > 20",  // false
                "100 > 50"
        );
        assertFalse(reader.readCondition(conditions).test(context));
    }

    @Test
    void testListConditionsWithPermissions() throws IOException {
        List<Object> conditions = List.of(
                "30 > 10",
                Map.of("permission", "mscript.test.permission1"),
                "player"
        );
        assertTrue(reader.readCondition(conditions).test(context));

        conditions = List.of(
                "30 > 10",
                Map.of("permission", "mscript.test.permission6"), // false
                "player"
        );
        assertFalse(reader.readCondition(conditions).test(context));
    }

    @Test
    void testPermissionCondition() throws IOException {
        assertTrue(reader.readCondition(Map.of("permission", "mscript.test.permission1")).test(context));
        assertTrue(reader.readCondition(Map.of("permission", "mscript.test.permission2")).test(context));
        assertFalse(reader.readCondition(Map.of("permission", "mscript.test.permission6")).test(context));
        assertFalse(reader.readCondition(Map.of("permission", "mscript.test.permission7")).test(context));

        // Server always has all permissions
        TestContext serverContext = new TestContext(TestSender.server(), null);
        assertTrue(reader.readCondition(Map.of("permission", "any.permission")).test(serverContext));
    }

    @Test
    void testChanceCondition() throws IOException {
        Condition chanceCondition = reader.readCondition(Map.of("chance", "100%"));
        Boolean result = chanceCondition.test(context);
        assertNotNull(result);
        assertTrue(result); // 100% = true

        chanceCondition = reader.readCondition(Map.of("chance", "0%"));
        result = chanceCondition.test(context);
        assertNotNull(result);
        assertFalse(result); // 0% = false

        chanceCondition = reader.readCondition(Map.of("chance", "50%"));
        result = chanceCondition.test(context);
        assertNotNull(result);
    }

    @Test
    void testComplexConditionsWithReplacements() throws IOException {
        assertTrue(reader.readCondition("({test_integer} > 1000) && (player)").test(context));
        assertTrue(reader.readCondition("({test_integer2} == 5000) || (10 > 20)").test(context));
        assertFalse(reader.readCondition("({test_integer3} > 100) && (player)").test(context));
    }

    @Test
    void testNestedConditionsInList() throws IOException {
        List<Object> conditions = List.of(
                Map.of("permission", "mscript.test.permission1"),
                "({test_integer} > 1000) && ({test_integer2} > 100)",
                "player"
        );
        assertTrue(reader.readCondition(conditions).test(context));
    }
}
