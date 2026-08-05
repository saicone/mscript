package com.saicone.mscript.test;

import com.saicone.mscript.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestContext implements Context {

    public static Map<String, String> REPLACEMENTS = Map.ofEntries(
            Map.entry("{test_string1}", "STRING"),
            Map.entry("{test_string2}", "S T R I N G"),
            Map.entry("{test_string3}", "{test_string3}"),
            Map.entry("{test_boolean_true}", "true"),
            Map.entry("{test_boolean_false}", "false"),
            Map.entry("{test_boolean_yes}", "yes"),
            Map.entry("{test_boolean_no}", "no"),
            Map.entry("{test_integer}", "1234"),
            Map.entry("{test_integer2}", "5000"),
            Map.entry("{test_integer3}", "42"),
            Map.entry("{test_decimal}", "1234.56")
    );

    private final TestSender source;
    private TestSender agent;

    private long delay = 0;

    public TestContext(@NotNull TestSender source, @Nullable TestSender agent) {
        this.source = source;
        this.agent = agent;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public @NotNull TestSender source() {
        return source;
    }

    @Override
    public @Nullable TestSender agent() {
        return agent;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        final TestSender sender = get();
        return sender.getUniqueId();
    }

    @Override
    public @NotNull String parse(@NotNull String str) {
        String result = str;
        result = result.replace("{delay}", String.valueOf(delay));
        for (Map.Entry<String, String> entry : REPLACEMENTS.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public void sync(@NotNull Runnable command) {
        Context.super.sync(command);
    }

    @Override
    public void async(@NotNull Runnable command) {
        Context.super.async(command);
    }

    @Override
    public void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        this.delay += unit.toMillis(time);
        command.run();
    }

    @Override
    public void delayAsync(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        this.delay += unit.toMillis(time);
        command.run();
    }
}
