package br.com.game.script;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import br.com.engine.core.GameObject;
import br.com.engine.core.Vector2;

class AndarEmTileTest
{
    @Test void reachesNegativeAndPositiveTargetsWithoutOvershootingAtDifferentRates()
    {
        for (int hz : new int[] {30,60,144})
        {
            GameObject object = new GameObject();
            AndarEmTile movement = new AndarEmTile();
            object.addComponente(movement);
            object.setup();
            movement.mover(new Vector2(-60, 60), new Vector2(60, 60));
            for (int i = 0; i < hz; i++) movement.fixedUpdate(1f / hz);
            assertEquals(-60, object.getPosition().x, 0.001f);
            assertEquals(60, object.getPosition().y, 0.001f);
            movement.fixedUpdate(1f / hz);
            assertEquals(-60, object.getPosition().x);
            assertEquals(60, object.getPosition().y);
            object.dispose();
        }
    }

    @Test void copiesTargetAndRejectsAnUnreachableAxis()
    {
        GameObject object = new GameObject();
        AndarEmTile movement = new AndarEmTile();
        object.addComponente(movement);
        Vector2 target = new Vector2(1, 0);
        movement.mover(target, new Vector2(60, 0));
        target.x = 1000;
        movement.fixedUpdate(1f / 60);
        assertEquals(1f, object.getPosition().x);
        assertThrows(IllegalArgumentException.class, () -> movement.mover(new Vector2(5, 0), new Vector2()));
        object.dispose();
    }
}
