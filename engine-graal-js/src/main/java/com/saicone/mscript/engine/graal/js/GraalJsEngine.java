package com.saicone.mscript.engine.graal.js;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.saicone.mscript.Engine;
import com.saicone.mscript.context.EngineContext;
import com.saicone.mscript.util.Memory;
import com.saicone.mscript.util.cache.Cache;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.time.Duration;

@ApiStatus.Experimental
public final class GraalJsEngine implements Engine {

    // Default values to hold a maximum 1000-10000 scripts on cache
    private static final int MAX_WEIGHT = 5_000_000;
    private static final int MAX_CACHEABLE_LENGTH = 50_000;

    // Script wrapper to ensure local context on each execution
    private static final String WRAPPER_PREFIX = "(function(){'use strict';";
    private static final String WRAPPER_SUFFIX = "\n})()";

    @NotNull
    public static GraalJsEngine simple() {
        return simple(MAX_WEIGHT, MAX_CACHEABLE_LENGTH);
    }

    @NotNull
    public static GraalJsEngine simple(@NotNull GraalJSScriptEngine scriptEngine) {
        return simple(scriptEngine, MAX_WEIGHT, MAX_CACHEABLE_LENGTH);
    }

    @NotNull
    public static GraalJsEngine simple(long maxWeight, int maxCacheableLength) {
        final Context.Builder builder = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(name -> true);
        final GraalJSScriptEngine scriptEngine = GraalJSScriptEngine.create(null, builder);
        return simple(scriptEngine, maxWeight, maxCacheableLength);
    }

    @NotNull
    public static GraalJsEngine simple(@NotNull GraalJSScriptEngine scriptEngine, long maxWeight, int maxCacheableLength) {
        final GraalJsEngine engine = new GraalJsEngine(scriptEngine);

        // Add memory if any cache library is available
        if (Cache.isAvailable()) {
            final Cache<String, CompiledScript> cache = Cache.<String, CompiledScript>newBuilder()
                    .maximumWeight(maxWeight)
                    .weigher((script, compiled) -> Math.max(1, script.length()))
                    .expireAfterAccess(Duration.ofMinutes(10))
                    .build();

            engine.setMemory(new Memory<>(
                    script -> script.length() <= maxCacheableLength,
                    engine::compile,
                    cache
            ));
        }

        return engine;
    }

    private final GraalJSScriptEngine scriptEngine;
    private Memory<String, CompiledScript> memory;

    public GraalJsEngine(@NotNull GraalJSScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    @NotNull
    public GraalJSScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    @Nullable
    public Memory<String, CompiledScript> getMemory() {
        return memory;
    }

    public void setMemory(@Nullable Memory<String, CompiledScript> memory) {
        this.memory = memory;
    }

    @Override
    public @UnknownNullability Object eval(@NotNull EngineContext context, @NotNull String script) throws Throwable {
        final CompiledScript compiled;
        if (memory == null) {
            compiled = compile(script);
        } else {
            compiled = memory.run(script);
        }

        final ScriptContext scriptContext = context.scriptContext();
        synchronized (scriptContext) {
            return compiled.eval(scriptContext);
        }
    }

    @NotNull
    public CompiledScript compile(@NotNull String script) {
        try {
            return scriptEngine.compile(WRAPPER_PREFIX + script + WRAPPER_SUFFIX);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        memory.close();
        scriptEngine.close();
    }
}
