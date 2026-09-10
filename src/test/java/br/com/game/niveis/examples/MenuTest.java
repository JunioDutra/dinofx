package br.com.game.niveis.examples;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.google.gson.Gson;
import br.com.engine.resources.ScenesDefinition;

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
}
