package com.badlogic.starfishcollector;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ZoroCollectorBeta extends GameBeta {
    private MainCharacter mainCharacter;
    private boolean win;
    private boolean gameOver;

    public void initialize() {
        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("grass.jpg");
        grass.setSize(800, 800);
        BaseActor.setWorldBounds(grass);

        new Zoro(400, 400, mainStage);
        new Zoro(500, 100, mainStage);
        new Zoro(100, 450, mainStage);
        new Zoro(200, 250, mainStage);

        new Rock(200, 150, mainStage);
        new Rock(100, 300, mainStage);
        new Rock(300, 350, mainStage);
        new Rock(450, 200, mainStage);

        new Orc(300, 200, mainStage);
        new Orc(400, 300, mainStage);

        mainCharacter = new MainCharacter(20, 20, mainStage);
        win = false;
        gameOver = false;
    }

    public void update(float dt) {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.starfishcollector.Rock"))
            mainCharacter.preventOverlap(rockActor);
        for (BaseActor zoroActor : BaseActor.getList(mainStage, "com.badlogic.starfishcollector.Zoro")) {
            try {
                // Завантаження класу через Class.forName()
                Class<?> zoroClass = Class.forName("com.badlogic.starfishcollector.Zoro");

                // Перевірка зіткнення з Zoro
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
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            }
        }
        for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.starfishcollector.Orc")) {
            Orc orc = (Orc) orcActor;
            if (mainCharacter.overlaps(orc) && !gameOver) {
                gameOver = true;
                mainCharacter.remove();

                BaseActor gameOverMessage = new BaseActor(0, 0, mainStage);
                gameOverMessage.loadTexture("game-over.png");
                gameOverMessage.centerAtPosition(400, 300);
                gameOverMessage.setOpacity(0);
                gameOverMessage.addAction(Actions.fadeIn(1));
            }
        }
        if (BaseActor.count(mainStage, "com.badlogic.starfishcollector.Zoro") == 0 && !win) {
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
