package br.com.game.niveis.examples;

import java.util.ArrayList;
import java.util.List;
import br.com.engine.componentes.audio.AudioEffect;
import br.com.engine.componentes.builders.ScriptBuilder;
import br.com.engine.componentes.drawable.SpriteFont;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.graphics.Color;
import br.com.engine.input.KeyBoard;
import br.com.engine.input.KeyCode;
import br.com.engine.resources.ScenesDefinition;

public class Menu extends Scene
{
    private boolean isInitializedBGSound;
    private AudioEffect bgSound;
    private int current;
    private final List<SpriteFont> fonts = new ArrayList<>();
    private List<Integer> sceneIndices = List.of();

    static List<Integer> visibleSceneIndices(List<ScenesDefinition> definitions)
    {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < definitions.size(); i++) if (definitions.get(i).isMenu()) indices.add(i);
        return List.copyOf(indices);
    }

    @Override public void setup()
    {
        super.setup();
        SpriteFont title = new SpriteFont("fonts/font.ttf", 50);
        GameObject heading = new GameObject("itensMenu");
        heading.addComponente(title);
        title.setText("O Jogo da LURYA!!!");
        title.setColor(Color.RED);
        heading.getPosition().setPosition(10, 0);
        add(heading);

        var definitions = ControleBase.getInstance().getSceneDefinitions();
        sceneIndices = visibleSceneIndices(definitions);
        for (int index : sceneIndices) createItemMenu((fonts.size() + 1) + "- " + definitions.get(index).getTitle());

        GameObject audio = new GameObject("audio");
        bgSound = new AudioEffect("audio/rainy_city.wav");
        audio.addComponente(bgSound);
        add(audio);
    }

    private void createItemMenu(String text)
    {
        GameObject item = new GameObject("itensMenu");
        SpriteFont font = new SpriteFont("fonts/font.ttf", 50);
        item.addComponente(font);
        font.setText(text);
        fonts.add(font);
        int index = fonts.size() - 1;
        item.addComponente(ScriptBuilder.createNoTime(() -> font.setColor(current == index ? Color.YELLOW : Color.BLACK)));
        item.getPosition().setPosition(10, fonts.size() * 50 + 20);
        add(item);
    }

    private void select(int direction)
    {
        if (sceneIndices.isEmpty()) return;
        current = Math.floorMod(current + direction, sceneIndices.size());
    }

    @Override public void update(long time)
    {
        super.update(time);
        if (!isInitializedBGSound)
        {
            bgSound.setVolume(0.1);
            bgSound.play();
            isInitializedBGSound = true;
        }
        KeyBoard keyboard = KeyBoard.infInstace();
        if (keyboard.wasPressedThisFrame(KeyCode.DOWN)) select(1);
        if (keyboard.wasPressedThisFrame(KeyCode.UP)) select(-1);
        if (keyboard.wasPressedThisFrame(KeyCode.ENTER)) {
            if (!sceneIndices.isEmpty()) ControleBase.getInstance().nextScene(sceneIndices.get(current));
        }
    }
}
