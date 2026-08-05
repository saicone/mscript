package com.saicone.mscript;

import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.test.TestContext;
import com.saicone.mscript.test.TestSender;
import com.saicone.mscript.test.io.TestScriptReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptTest {

    private ScriptReader reader;
    private TestContext context;

    @BeforeEach
    void setUp() {
        reader = new TestScriptReader();
        context = new TestContext(TestSender.player("TestPlayer"), null);
    }

    @Test
    void testSimpleScript() throws IOException {
        List<Object> script = List.of(
            "message: First message",
            "message: Second message"
        );

        Execution execution = reader.readExecution(script);
        Result result = execution.run(context);

        assertTrue(result.isDone());
    }

    @Test
    void testScriptWithIfElse() throws IOException {
        Map<String, Object> script = Map.of(
            "if", "30 > 10",
            "run", List.of("message: Condition is true"),
            "else", List.of("message: Condition is false")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Condition is true", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithFalseCondition() throws IOException {
        Map<String, Object> script = Map.of(
            "if", "10 > 30",
            "run", List.of("message: Condition is true"),
            "else", List.of("message: Condition is false")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Condition is false", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithReplacements() throws IOException {
        Map<String, Object> script = Map.of(
            "if", "\"{test_integer}\" > \"1000\"",
            "run", List.of("message: Value is {test_integer}")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Value is 1234", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testComplexScript() throws IOException {
        List<Object> script = List.of(
            "message: Starting script",
            Map.of(
                "if", "player",
                "run", List.of(
                    "message: You are a player",
                    "actionbar: Player confirmed"
                )
            ),
            "delay: 1 SECONDS",
            "message: Script finished"
        );

        context.setDelay(0);
        Execution execution = reader.readExecution(script);
        Result result = execution.run(context);

        assertEquals(1000, Long.parseLong(context.parse("{delay}")));
        // the last result on synchronized execution should be the delay, since the track got separated in that place
        assertTrue(result.isDelayed());
        // while on async execution we can get the real last result since we wait until the execution finish
        assertTrue(execution.runAsync(context).join().isDone());
    }

    @Test
    void testScriptWithBreak() throws IOException {
        List<Object> script = List.of(
            "message: First",
            "break",
            "message: This should not execute"
        );

        Execution execution = reader.readExecution(script);
        Result result = execution.run(context);

        // the break result doesn't actually return itself, it only breaks the track
        assertTrue(result.isDone());
        Component message = context.source().actualMessage();
        assertEquals("First", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithReturn() throws IOException {
        List<Object> script = List.of(
            "message: First",
            "return: test_value",
            "message: This should not execute"
        );

        Execution execution = reader.readExecution(script);
        Result result = execution.run(context);

        assertTrue(result.isReturn());
        assertEquals("test_value", result.value());
    }

    @Test
    void testNestedConditions() throws IOException {
        Map<String, Object> innerCondition = Map.of(
            "if", "\"{test_boolean_true}\" == \"true\"",
            "run", List.of("message: Nested condition passed")
        );

        Map<String, Object> outerCondition = Map.of(
            "if", "player",
            "run", List.of(
                "message: Player check passed",
                innerCondition
            )
        );

        Execution execution = reader.readExecution(outerCondition);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Nested condition passed", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithListConditions() throws IOException {
        Map<String, Object> script = Map.of(
            "if", List.of(
                "30 > 20",
                Map.of("permission", "mscript.test.permission2"),
                "10 > 5"
            ),
            "run", List.of("message: &aAll conditions passed")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("All conditions passed", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithListConditionsFailure() throws IOException {
        Map<String, Object> script = Map.of(
            "if", List.of(
                "30 > 20",
                Map.of("permission", "mscript.test.permission6"),  // This is false
                "10 > 5"
            ),
            "run", List.of("message: This should not execute"),
            "else", List.of("message: Condition failed as expected")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Condition failed as expected", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithLegacyMessages() throws IOException {
        List<Object> script = List.of(
            "message: &a&lGreen Bold",
            "message: §c§oRed Italic",
            "message: <blue><bold>MiniMessage Blue Bold</bold></blue>"
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        String plainText = PlainTextComponentSerializer.plainText().serialize(message);
        assertTrue(plainText.contains("MiniMessage Blue Bold"));
    }

    @Test
    void testScriptWithChanceCondition() throws IOException {
        Map<String, Object> script = Map.of(
            "if", Map.of("chance", "100%"),
            "run", List.of("message: This always executes")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("This always executes", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithPermissionCheck() throws IOException {
        Map<String, Object> script = Map.of(
            "if", Map.of("permission", "mscript.test.permission1"),
            "run", List.of("message: &eYou have permission"),
            "else", List.of("message: &cNo permission")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("You have permission", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithParenthesesCondition() throws IOException {
        Map<String, Object> script = Map.of(
            "if", "({test_integer} > 1000) && ({test_integer2} == 5000)",
            "run", List.of("message: &aComplex condition passed")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Complex condition passed", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testScriptWithReplacementsNoQuotes() throws IOException {
        Map<String, Object> script = Map.of(
            "if", "{test_integer} == 1234",
            "run", List.of("message: Value is {test_integer}")
        );

        Execution execution = reader.readExecution(script);
        execution.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Value is 1234", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testComplexScriptWithMultipleFeatures() throws IOException {
        List<Object> script = List.of(
            "message: &6&lStarting complex script",
            Map.of(
                "if", List.of(
                    "player",
                    "{test_integer} > 1000",
                    Map.of("permission", "mscript.test.permission1")
                ),
                "run", List.of(
                    "message: <green>All checks passed!</green>",
                    "actionbar: &e{test_string1}",
                    "delay: 2 SECONDS",
                    "message: §bDelayed message"
                )
            ),
            "message: &aScript finished"
        );

        context.setDelay(0);
        Execution execution = reader.readExecution(script);
        Result result = execution.runAsync(context).join();

        assertEquals(2000L, Long.parseLong(context.parse("{delay}")));
        Component message = context.source().actualMessage();
        String plainText = PlainTextComponentSerializer.plainText().serialize(message);
        assertTrue(plainText.contains("Script finished"));
    }

    @Test
    void testScriptWithServerContext() throws IOException {
        TestContext serverContext = new TestContext(TestSender.server(), null);

        Map<String, Object> script = Map.of(
            "if", "console",
            "run", List.of("message: &eRunning as server"),
            "else", List.of("message: &cRunning as player")
        );

        Execution execution = reader.readExecution(script);
        execution.run(serverContext);

        Component message = serverContext.source().actualMessage();
        assertEquals("Running as server", PlainTextComponentSerializer.plainText().serialize(message));
    }
}
