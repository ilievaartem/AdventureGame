package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class ZoroCollectorBeta extends GameBeta {
    private Character mainCharacter;
    private ActorBeta zoro;
    private ActorBeta grass;
    private ActorBeta winMessage;
    private boolean win;

    public void initialize() {
        grass = new ActorBeta();
        grass.setTexture(new Texture(Gdx.files.internal("grass.png")));
        mainStage.addActor(grass);
        zoro = new ActorBeta();
        zoro.setTexture(new Texture(Gdx.files.internal("zoro-1.png")));
        zoro.setPosition(380, 380);
        mainStage.addActor(zoro);
        mainCharacter = new Character();
        mainCharacter.setTexture(new Texture(Gdx.files.internal("main-character-1.png")));
        mainCharacter.setPosition(20, 20);
        mainStage.addActor(mainCharacter);
        winMessage = new ActorBeta();
        winMessage.setTexture(new Texture(Gdx.files.internal("you-win.png")));
        winMessage.setPosition(180, 180);
        winMessage.setVisible(false);
        mainStage.addActor(winMessage);
        win = false;
    }

    public void update(float dt) {
        if (mainCharacter.overlaps(zoro))
        {
            zoro.remove();
            winMessage.setVisible(true);
        }
    }
}
