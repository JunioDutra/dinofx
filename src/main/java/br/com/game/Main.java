package br.com.game;

import br.com.engine.main.Executor;
import br.com.engine.core.SceneRegistry;
import br.com.game.niveis.examples.Level001;
import br.com.game.niveis.examples.Level002;
import br.com.game.niveis.examples.LuaHiddenDemo;
import br.com.game.niveis.examples.Menu;
import br.com.game.niveis.examples.QuedaLivre;
import br.com.game.niveis.examples.Spaceship;
import br.com.game.niveis.examples.TiledMapGame;

public class Main
{
    public static void main(String[] args)
    {
        Executor.loadGame(args, scenes());
    }

    /** Every id in application.json is bound here without class-name reflection. */
    public static SceneRegistry scenes()
    {
        return new SceneRegistry()
            .register("dinofx:menu", Menu::new)
            .register("dinofx:spaceship", Spaceship::new)
            .register("dinofx:free-fall", QuedaLivre::new)
            .register("dinofx:level-001", Level001::new)
            .register("dinofx:level-002", Level002::new)
            .register("dinofx:tiled-map", TiledMapGame::new)
            .register("dinofx:lua-hidden-demo", LuaHiddenDemo::new);
    }
}
