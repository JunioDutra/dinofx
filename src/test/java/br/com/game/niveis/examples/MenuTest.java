package br.com.game.niveis.examples;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import br.com.engine.resources.ScenesDefinition;
import br.com.engine.input.KeyBoard;
import br.com.engine.input.KeyCode;

class MenuTest
{
    @Test void preservesSceneTargetsWhenMenuEntriesAreHidden()
    {
        ScenesDefinition visible = new ScenesDefinition("visible", "java");
        ScenesDefinition hidden = new ScenesDefinition("hidden", "java");
        hidden.setMenu(false);
        assertEquals(List.of(0,2), Menu.visibleSceneIndices(List.of(visible, hidden, visible)));
        assertEquals(List.of(), Menu.visibleSceneIndices(List.of(hidden)));
    }

    @Test void oldConfigurationWithoutMenuMetadataRemainsVisible()
    {
        ScenesDefinition legacy = new Gson().fromJson("{\"scene\":\"old\",\"type\":\"java\"}", ScenesDefinition.class);
        assertTrue(legacy.isMenu());
        assertEquals("old", legacy.getTitle());
    }

    @Test void acceptsFirstAndRapidPressesWithoutRepeatingHeldKeys() throws Exception
    {
        Menu menu = new Menu();
        var entries = Menu.class.getDeclaredField("sceneIndices");
        entries.setAccessible(true);
        entries.set(menu, List.of(1, 2, 3));
        var audioStarted = Menu.class.getDeclaredField("isInitializedBGSound");
        audioStarted.setAccessible(true);
        audioStarted.setBoolean(menu, true);
        var current = Menu.class.getDeclaredField("current");
        current.setAccessible(true);
        KeyBoard keyboard = KeyBoard.infInstace();
        keyboard.reset();
        try
        {
            keyboard.press(KeyCode.DOWN);
            keyboard.beginFrame();
            menu.update(16);
            assertEquals(1, current.getInt(menu));
            keyboard.endFrame();
            keyboard.beginFrame();
            menu.update(16);
            assertEquals(1, current.getInt(menu));
            keyboard.endFrame();
            keyboard.release(KeyCode.DOWN);
            keyboard.press(KeyCode.DOWN);
            keyboard.beginFrame();
            menu.update(16);
            assertEquals(2, current.getInt(menu));
        }
        finally { keyboard.reset(); }
    }
}
