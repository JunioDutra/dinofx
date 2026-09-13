package br.com.game;

import br.com.engine.core.ControleBase;
import br.com.engine.platform.lwjgl.*;
import br.com.engine.resources.ResourceManager;
import br.com.game.niveis.examples.LuaHiddenDemo;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;

/** Run against the packaged jar from an empty directory. Requires a Vulkan-capable device. */
public final class GameMigrationSmokeApp
{
    public static void main(String[] args) throws Exception
    {
        System.setProperty("org.lwjgl.system.memoryBackend", System.getProperty("org.lwjgl.system.memoryBackend", "ffm"));
        var callback = GLFWErrorCallback.createPrint(System.err).set();
        ControleBase control = null;
        boolean completed = false;
        int expectedLuaDisposals = 0;
        try
        {
            if (!glfwInit()) throw new IllegalStateException("GLFW initialization failed");
            var image = ResourceManager.image("imagens/hero_sheet.png");
            if (image != ResourceManager.image("imagens/hero_sheet.png")) throw new AssertionError("Packaged image cache failed");
            control = ControleBase.getInstance();
            LuaHiddenDemo.resetSmokeEvidence();
            int sceneCount = control.getConfigurations().getScenes().size();
            int firstScene = Integer.getInteger("enginefx.smoke.firstScene", 0);
            int lastScene = Integer.getInteger("enginefx.smoke.lastScene", sceneCount - 1);
            if (firstScene < 0 || lastScene >= sceneCount || firstScene > lastScene)
                throw new IllegalArgumentException("Invalid smoke scene range: " + firstScene + ".." + lastScene);

            var graphics = new VulkanGraphicsContext();
            graphics.setCanvasSize((int)control.getScreen().getWidth(), (int)control.getScreen().getHeight());
            control.getScreen().setGraphicsContext(graphics);
            try (var instance = new LwjglVulkanInstance("game-migration-smoke");
                 var window = new LwjglVulkanWindow(instance, 680, 650, "Migration smoke");
                 var device = new LwjglVulkanDevice(instance, window.getSurface());
                 var renderer = new LwjglVulkanRenderSession(device, window))
            {
                var report = device.getReport();
                System.out.println("DEVICE " + report.deviceName() + " api=" + report.apiVersion() + " driver=" + report.driverVersion()
                    + " presentMode=" + System.getProperty("enginefx.vulkan.presentMode", "auto"));
                if (Boolean.getBoolean("enginefx.smoke.hidden")) glfwHideWindow(window.getHandle());
                try (var vertices = new LwjglVulkanDynamicVertexBuffer(device, 16))
                {
                    vertices.upload(new float[48], 48);
                    if (vertices.getCapacityBytes() < 192) throw new AssertionError("Vertex buffer growth failed");
                    vertices.upload(new float[4], 4);
                }
                control.setup();
                for (int pass = 0; pass < 2; pass++)
                {
                    for (int scene = firstScene; scene <= lastScene; scene++)
                    {
                        control.nextScene(scene);
                        for (int frame = 0; frame < 120; frame++)
                        {
                            window.pollEvents();
                            if (window.shouldClose()) throw new AssertionError("Smoke closed early");
                            if (frame == 40) glfwSetWindowSize(window.getHandle(), 720, 540);
                            if (frame == 80) glfwSetWindowSize(window.getHandle(), 680, 650);
                            control.processLogics();
                            graphics.beginFrame();
                            control.renderGraphics();
                            renderer.drawFrame(graphics);
                        }
                        String expected = control.getConfigurations().getScenes().get(scene).getScene();
                        if (!control.getCurrentScene().getClass().getName().equals(expected)) throw new AssertionError("Scene did not load: " + expected);
                        if (control.getCurrentScene() instanceof LuaHiddenDemo demo)
                        {
                            demo.assertLuaCallbacks();
                            expectedLuaDisposals++;
                        }
                        System.out.println("PASS " + pass + " " + expected);
                    }
                }
                completed = true;
                System.out.println("PASS migration: " + (lastScene - firstScene + 1) + " scenes twice, packaged assets/cache"
                    + (expectedLuaDisposals > 0 ? ", Lua lifecycle" : "") + ", vertex growth and resize");
            }
        }
        finally
        {
            try
            {
                if (control != null) control.stop();
                if (completed) LuaHiddenDemo.assertSmokeDisposals(expectedLuaDisposals);
            }
            finally { glfwTerminate(); glfwSetErrorCallback(null); callback.free(); }
        }
    }
}
