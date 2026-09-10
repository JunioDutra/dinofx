package br.com.game.script;

import br.com.engine.componentes.SimpleComponent;
import br.com.engine.core.Vector2;

/** Moves towards a target using independent axis speeds in pixels/second. */
public class AndarEmTile extends SimpleComponent
{
    private Vector2 pontoa;
    private Vector2 velocidade;

    @Override public void setup() { }
    @Override public void update(long time) { }
    @Override public void draw() { }

    public void mover(Vector2 target, Vector2 speed)
    {
        if (target == null || speed == null || !Float.isFinite(target.x) || !Float.isFinite(target.y) ||
            !Float.isFinite(speed.x) || !Float.isFinite(speed.y))
            throw new IllegalArgumentException("Target and speed must be finite");
        Vector2 position = getParent().getPosition();
        if ((target.x != position.x && speed.x == 0) || (target.y != position.y && speed.y == 0))
            throw new IllegalArgumentException("Moving axes require non-zero speed");
        pontoa = new Vector2(target.x, target.y);
        velocidade = new Vector2(Math.abs(speed.x), Math.abs(speed.y));
    }

    @Override public void fixedUpdate(float deltaSeconds)
    {
        if (pontoa == null) return;
        Vector2 position = getParent().getPosition();
        position.x = moveTowards(position.x, pontoa.x, velocidade.x * deltaSeconds);
        position.y = moveTowards(position.y, pontoa.y, velocidade.y * deltaSeconds);
        if (position.x == pontoa.x && position.y == pontoa.y) pontoa = null;
    }

    private static float moveTowards(float value, float target, float distance)
    {
        if (Math.abs(target - value) <= distance) return target;
        return value + Math.signum(target - value) * distance;
    }
}
