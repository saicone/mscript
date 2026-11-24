package com.saicone.mscript.impl;

import com.saicone.mscript.Condition;
import com.saicone.mscript.Context;
import com.saicone.mscript.Engine;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.context.EngineContext;
import com.saicone.mscript.io.SectionReader;
import com.saicone.types.TypeParser;
import com.saicone.types.Types;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A script section representing both condition and execution for provided engine script execution.
 *
 * @author Rubenicos
 */
public class EngineSection implements Condition, Execution {

    /**
     * Create an {@link EngineSection} reader with provided parameters.
     *
     * @param engine the engine to evaluate scripts
     * @param regex  the regex pattern to match identifiers
     * @return       a newly generated section reader
     */
    @NotNull
    public static SectionReader<EngineSection> reader(@NotNull Engine engine, @NotNull @Language("RegExp") String regex) {
        return reader(engine, regex, "script");
    }

    /**
     * Create an {@link EngineSection} reader with provided parameters.
     *
     * @param engine     the engine to evaluate scripts
     * @param regex      the regex pattern to match identifiers
     * @param defaultKey the default key to use when reading from a map
     * @return           a newly generated section reader
     */
    @NotNull
    public static SectionReader<EngineSection> reader(@NotNull Engine engine, @NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
        return new Reader(engine, regex, defaultKey);
    }

    private final Engine engine;
    private final String script;

    /**
     * Constructs an engine section.
     *
     * @param engine the engine to evaluate scripts
     * @param script the script to evaluate
     */
    public EngineSection(@NotNull Engine engine, @NotNull String script) {
        this.engine = engine;
        this.script = script;
    }

    /**
     * Get the engine to evaluate scripts.
     *
     * @return a script engine
     */
    @NotNull
    public Engine getEngine() {
        return engine;
    }

    /**
     * Get the script to be evaluated.
     *
     * @return a script
     */
    @NotNull
    public String getScript() {
        return script;
    }

    @Override
    public @Nullable Boolean test(@NotNull Context context) {
        if (context instanceof EngineContext) {
            try {
                final Object result = engine.eval((EngineContext) context, script);
                if (result == null) {
                    return null;
                }
                return Types.BOOLEAN.parseOrThrow(result);
            } catch (Throwable t) {
                throw new RuntimeException("There was an error while evaluating a script condition", t);
            }
        } else {
            return null;
        }
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        if (context instanceof EngineContext) {
            try {
                final Object result = engine.eval((EngineContext) context, script);
                if (result == null) {
                    return Result.UNKNOWN;
                }

                if (result instanceof Result) {
                    return (Result) result;
                }
                if (result instanceof String) {
                    switch (((String) result).toLowerCase()) {
                        case "done":
                            return Result.DONE;
                        case "continue":
                            return Result.CONTINUE;
                        case "break":
                            return Result.BREAK;
                        case "return":
                            return Result.RETURN;
                        default:
                            break;
                    }
                }

                return Result.value(result);
            } catch (Throwable t) {
                throw new RuntimeException("There was an error while evaluating a script execution", t);
            }
        } else {
            return Result.UNKNOWN;
        }
    }

    private static class Reader extends SectionReader<EngineSection> {

        private static final TypeParser<List<String>> LIST_PARSER = Types.STRING.list();

        private final Engine engine;

        public Reader(@NotNull Engine engine, @NotNull @Language("RegExp") String regex) {
            this(engine, regex, "script");
        }

        public Reader(@NotNull Engine engine, @NotNull @Language("RegExp") String regex, @NotNull String defaultKey) {
            super(regex, defaultKey);
            this.engine = engine;
        }

        @NotNull
        public Engine getEngine() {
            return engine;
        }

        @Override
        protected EngineSection read(@NotNull String id, @NotNull String context) {
            return new EngineSection(this.engine, context);
        }

        @Override
        protected EngineSection read(@NotNull String id, @NotNull List<?> context) {
            return new EngineSection(this.engine, String.join("\n", LIST_PARSER.parseOrThrow(context)));
        }
    }
}
