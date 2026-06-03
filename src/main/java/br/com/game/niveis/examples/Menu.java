package br.com.game.niveis.examples;

import java.util.ArrayList;
import java.util.List;

import br.com.engine.componentes.audio.AudioEffect;
import br.com.engine.componentes.builders.ScriptBuilder;
import br.com.engine.componentes.drawable.SpriteFont;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.core.annotation.Bootable;
import br.com.engine.input.KeyBoard;
import br.com.engine.graphics.Color;
import br.com.engine.input.KeyCode;

@Bootable
public class Menu extends Scene {
    private boolean isInitializedBGSound;
    private AudioEffect bgSound;

    private int current = 0;
    private List<SpriteFont> fonts = new ArrayList<>();

    private float transitionTime = 100;
    private float elapsedTransitionTime = 0;

    @Override
    public void setup() {
        super.setup();

        SpriteFont font = new SpriteFont("font", 50);
        GameObject itemMenu0 = new GameObject("itensMenu");
        itemMenu0.addComponente(font);
        font.setText("O Jogo da LURYA!!!");
        font.setColor(Color.RED);
        itemMenu0.getPosition().setPosition(10, 0);
        add(itemMenu0);

        createItemMenu("1- Menu");
        createItemMenu("2- Spaceship");
        createItemMenu("3- QuedaLivre");
        createItemMenu("4- Level001");
        createItemMenu("5- Level002");
        createItemMenu("6- TiledMapGame");

        GameObject obj = new GameObject("audio");
        this.bgSound = new AudioEffect("rainy_city.wav");
        obj.addComponente(this.bgSound);
        add(obj);
    }

    private void createItemMenu(String text) {
        GameObject objItemMenu = new GameObject("itensMenu");
        SpriteFont font = new SpriteFont("font", 50);
        objItemMenu.addComponente(font);
        font.setText(text);
        fonts.add(font);
        int index = fonts.size() - 1;
        objItemMenu.addComponente(ScriptBuilder.createNoTime(()->{
            if( current == index ) {
                font.setColor(Color.YELLOW);
            } else {
                font.setColor(Color.BLACK);
            }
        }));
        objItemMenu.getPosition().setPosition(10, (fonts.size()*50)+20);
        add(objItemMenu);
    }
    
    @Override
    public void update(long time) {
        super.update(time);
        elapsedTransitionTime += time;

        if(!isInitializedBGSound) {
            this.bgSound.setVolume(0.1);
            this.bgSound.play();
            this.isInitializedBGSound = true;
        }

        KeyBoard.infInstace().ifKeyPressed(KeyCode.DOWN, ()->{
            if( elapsedTransitionTime > transitionTime ) {
                current += 1;
            }

            elapsedTransitionTime = 0;
        });

        KeyBoard.infInstace().ifKeyPressed(KeyCode.UP, ()->{
            if( elapsedTransitionTime > transitionTime ) {
                current -= 1;
            }

            elapsedTransitionTime = 0;
        });

        KeyBoard.infInstace().ifKeyPressed(KeyCode.ENTER, ()->{
            ControleBase.getInstance().nextScene(current);
        });

        if( current > this.fonts.size() - 1 || current < 0 ) {
            current = 0;
        }
    }
}
