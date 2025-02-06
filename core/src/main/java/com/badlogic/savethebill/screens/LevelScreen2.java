package com.badlogic.savethebill.screens;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.characters.MainCharacter;
import com.badlogic.savethebill.characters.Orc;
import com.badlogic.savethebill.objects.Rock;
import com.badlogic.savethebill.objects.Whirlpool;
import com.badlogic.savethebill.characters.Zoro;

public class LevelScreen2 extends BaseScreen {
    private MainCharacter mainCharacter;
    private boolean win;
    private boolean gameOver;

    public void initialize() {
        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("grass.jpg");
        grass.setSize(1200, 900);
        BaseActor.setWorldBounds(grass);

        new Zoro(200, 150, mainStage);
        new Zoro(100, 300, mainStage);
        new Zoro(300, 350, mainStage);
        new Zoro(450, 200, mainStage);
        new Zoro(850, 700, mainStage);

        new Rock(400, 400, mainStage);
        new Rock(500, 100, mainStage);
        new Rock(100, 450, mainStage);
        new Rock(200, 250, mainStage);
        new Rock(800, 750, mainStage);

        new Orc(200, 400, mainStage);
        new Orc(300, 400, mainStage);

        mainCharacter = new MainCharacter(20, 20, mainStage);
        win = false;
        gameOver = false;
    }

    public void update(float dt) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Rock"))
            mainCharacter.preventOverlap(rockActor);
        for (BaseActor zoroActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Zoro")) {
            Zoro zoro = (Zoro) zoroActor;
            if (mainCharacter.overlaps(zoro) && !zoro.collected) {
                zoro.collected = true;
                zoro.clearActions();
                zoro.addAction(Actions.fadeOut(1));
                zoro.addAction(Actions.after(Actions.removeActor()));

                Whirlpool whirl = new Whirlpool(0, 0, mainStage);
                whirl.centerAtActor(zoro);
                whirl.setOpacity(0.25f);
            }
        }
        for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
            Orc orc = (Orc) orcActor;
            if (mainCharacter.overlaps(orc) && !gameOver && !win) {
                gameOver = true;
                mainCharacter.remove();

                BaseActor gameOverMessage = new BaseActor(0, 0, mainStage);
                gameOverMessage.loadTexture("game-over.png");
                gameOverMessage.centerAtPosition(400, 300);
                gameOverMessage.setOpacity(0);
                gameOverMessage.addAction(Actions.fadeIn(1));
            }
        }
        if (BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Zoro") == 0 && !win) {
            win = true;
            BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(400, 300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }
    }
}
