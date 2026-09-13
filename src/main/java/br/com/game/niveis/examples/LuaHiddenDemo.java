package br.com.game.niveis.examples;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.engine.componentes.scripts.LuaComponent;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.resources.ClasspathResourceResolver;
import br.com.engine.scripting.ScriptApiRegistry;
import br.com.engine.scripting.ScriptContext;
import br.com.engine.scripting.ScriptModule;
import br.com.engine.scripting.ScriptRuntime;

/**
 * Hidden menu entry used by the packaged smoke to exercise the public Lua
 * component lifecycle without exposing a game object to the script.
 */
public final class LuaHiddenDemo extends Scene
{
    private static final AtomicInteger VERIFIED_DISPOSALS = new AtomicInteger();

    private ScriptRuntime runtime;
    private ScriptContext context;
    private boolean disposalVerified;

    @Override public void setup()
    {
        super.setup();
        if (runtime != null) return;
        ClasspathResourceResolver resolver = new ClasspathResourceResolver("dinofx", "3.0.0", "res");
        runtime = ScriptRuntime.lua(resolver, new ScriptApiRegistry());
        context = new ScriptContext(Set.of("engine.state"), Map.of(), null, null);
        ScriptModule module = runtime.loadModule(resolver.ref("scripts/hidden_demo.lua"));
        GameObject object = new GameObject("lua-hidden-demo");
        object.addComponente(new LuaComponent(runtime, module, context));
        add(object);
        if (!context.state("loaded").asBoolean()) throw new IllegalStateException("Lua hidden demo did not run setup");
    }

    /** Called by the smoke while this scene is current. */
    public void assertLuaCallbacks()
    {
        if (context == null || context.state("frames").asLong() <= 0)
            throw new IllegalStateException("Lua hidden demo did not receive update");
        if (context.state("last_delta").asDouble() <= 0)
            throw new IllegalStateException("Lua hidden demo did not receive a seconds delta");
    }

    @Override public void dispose()
    {
        RuntimeException failure = null;
        try { super.dispose(); }
        catch (RuntimeException exception) { failure = exception; }
        try
        {
            if (!disposalVerified)
            {
                if (context == null || !context.state("disposed").asBoolean())
                    throw new IllegalStateException("Lua hidden demo did not run dispose");
                disposalVerified = true;
                VERIFIED_DISPOSALS.incrementAndGet();
            }
        }
        catch (RuntimeException exception)
        {
            if (failure == null) failure = exception;
            else failure.addSuppressed(exception);
        }
        finally
        {
            if (runtime != null)
            {
                try { runtime.close(); }
                catch (RuntimeException exception)
                {
                    if (failure == null) failure = exception;
                    else failure.addSuppressed(exception);
                }
                runtime = null;
            }
        }
        if (failure != null) throw failure;
    }

    public static void resetSmokeEvidence() { VERIFIED_DISPOSALS.set(0); }

    public static void assertSmokeDisposals(int expectedMinimum)
    {
        if (VERIFIED_DISPOSALS.get() != expectedMinimum)
            throw new IllegalStateException("Lua hidden demo disposals: expected " + expectedMinimum
                + ", observed " + VERIFIED_DISPOSALS.get());
    }
}
