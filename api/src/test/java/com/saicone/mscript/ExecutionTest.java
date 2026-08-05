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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutionTest {

    private ScriptReader reader;
    private TestContext context;

    @BeforeEach
    void setUp() {
        reader = new ScriptReader();
        context = new TestContext(TestSender.player("TestPlayer"), null);
        context.setDelay(0);
    }

    @Test
    void testMessageExecution() throws IOException {
        Execution exec = reader.readExecution("message: Hello World");
        Result result = exec.run(context);

        assertTrue(result.isDone());
        Component message = context.source().actualMessage();
        assertNotNull(message);
        assertEquals("Hello World", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testMessageWithReplacements() throws IOException {
        Execution exec = reader.readExecution("message: Test {test_string1}");
        exec.run(context);

        Component message = context.source().actualMessage();
        assertEquals("Test STRING", PlainTextComponentSerializer.plainText().serialize(message));
    }

    @Test
    void testActionBarExecution() throws IOException {
        Execution exec = reader.readExecution("actionbar: Test ActionBar {test_integer}");
        exec.run(context);

        Component actionBar = context.source().actualActionBar();
        assertNotNull(actionBar);
        assertEquals("Test ActionBar 1234", PlainTextComponentSerializer.plainText().serialize(actionBar));
    }

    @Test
    void testDelayExecution() throws IOException {
        context.setDelay(0);

        Execution exec = reader.readExecution(List.of(
                "delay: 5 SECONDS",
                "message: Delayed message"
        ));
        Result result = exec.run(context);

        assertTrue(result.isDelayed());
        assertEquals(5, result.unit().toSeconds(result.time()));
        assertEquals(TimeUnit.MILLISECONDS, result.unit());
        assertEquals("5000", context.parse("{delay}"));
    }

    @Test
    void testDelayWithDuration() throws IOException {
        context.setDelay(0);

        Execution exec = reader.readExecution("delay: 10 SECONDS");
        Result result = exec.run(context);

        assertTrue(result.isDelayed());
        assertEquals(10000, result.time());
        assertEquals(TimeUnit.MILLISECONDS, result.unit());
    }

    @Test
    void testResultExecutions() throws IOException {
        assertTrue(reader.readExecution("done").run(context).isDone());
        assertTrue(reader.readExecution("continue").run(context).isContinue());
        assertTrue(reader.readExecution("break").run(context).isBreak());
        assertTrue(reader.readExecution("return").run(context).isReturn());
    }

    @Test
    void testReturnWithValue() throws IOException {
        Result result = reader.readExecution("return: test_value").run(context);

        assertTrue(result.isReturn());
        assertEquals("test_value", result.value());
    }

    @Test
    void testReturnWithReplacements() throws IOException {
        Result result = reader.readExecution("return: {test_integer}").run(context);

        assertTrue(result.isReturn());
        assertEquals("1234", result.value());
    }
}
