package com.saicone.mscript;

import com.saicone.mscript.io.ScriptReader;
import com.saicone.mscript.test.TestContext;
import com.saicone.mscript.test.TestSender;
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
        reader = new ScriptReader();
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
}
