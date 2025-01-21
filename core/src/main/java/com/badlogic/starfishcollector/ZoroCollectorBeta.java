package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ZoroCollectorBeta extends GameBeta {
    private Character mainCharacter;
    private ActorBeta zoro;
    private ActorBeta grass;
    private ActorBeta winMessage;
    private ActorBeta orc;
    private ActorBeta gameOverMessage;
    private boolean win;

    public void initialize() {
        // Додавання текстур
        grass = new ActorBeta();
        grass.setTexture(new Texture(Gdx.files.internal("grass-1.png")));
        mainStage.addActor(grass);

        zoro = new ActorBeta();
        zoro.setTexture(new Texture(Gdx.files.internal("zoro-1.png")));
        zoro.setPosition(380, 380);
        mainStage.addActor(zoro);

        mainCharacter = new Character();
        mainCharacter.setTexture(new Texture(Gdx.files.internal("character-2.png")));
        mainCharacter.setPosition(20, 20);
        mainStage.addActor(mainCharacter);

        winMessage = new ActorBeta();
        winMessage.setTexture(new Texture(Gdx.files.internal("you-win.png")));
        winMessage.setPosition(180, 180);
        winMessage.setVisible(false);
        mainStage.addActor(winMessage);

        orc = new ActorBeta();
        orc.setTexture(new Texture(Gdx.files.internal("orc-1.png")));
        orc.setPosition(200, 200);
        mainStage.addActor(orc);

        gameOverMessage = new ActorBeta();
        gameOverMessage.setTexture(new Texture(Gdx.files.internal("game-over.png")));
        gameOverMessage.setPosition(150, 180);
        gameOverMessage.setVisible(false);
        mainStage.addActor(gameOverMessage);

        win = false;
    }

    public void update(float dt) {
        // Перевірка, чи персонаж торкнувся зорро
        if (mainCharacter.overlaps(zoro)) {
            zoro.remove();
            winMessage.setVisible(true);
            win = true;
        }

        // Перевірка, чи персонаж торкнувся орка
        if (mainCharacter.overlaps(orc)) {
            mainCharacter.remove(); // Видалення персонажа зі сцени
            gameOverMessage.setVisible(true); // Відображення повідомлення "Game Over"
        }
    }
}
