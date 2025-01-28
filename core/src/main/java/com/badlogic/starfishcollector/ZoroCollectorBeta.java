package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.starfishcollector.Rock;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class ZoroCollectorBeta extends GameBeta {
//    private Character mainCharacter;
//    private ActorBeta zoro;
//    private ActorBeta grass;
//    private ActorBeta winMessage;
//    private ActorBeta orc;
//    private ActorBeta gameOverMessage;
//    private boolean win;

    private MainCharacter mainCharacter;
    private boolean win;
    public void initialize() {
//        // Додавання текстур
//        grass = new ActorBeta();
//        grass.setTexture(new Texture(Gdx.files.internal("grass-1.png")));
//        mainStage.addActor(grass);
//
//        zoro = new ActorBeta();
//        zoro.setTexture(new Texture(Gdx.files.internal("zoro-1.png")));
//        zoro.setPosition(380, 380);
//        mainStage.addActor(zoro);
//
//        mainCharacter = new Character();
//        mainCharacter.setTexture(new Texture(Gdx.files.internal("character-2.png")));
//        mainCharacter.setPosition(20, 20);
//        mainStage.addActor(mainCharacter);
//
//        winMessage = new ActorBeta();
//        winMessage.setTexture(new Texture(Gdx.files.internal("you-win.png")));
//        winMessage.setPosition(180, 180);
//        winMessage.setVisible(false);
//        mainStage.addActor(winMessage);
//
//        orc = new ActorBeta();
//        orc.setTexture(new Texture(Gdx.files.internal("orc-1.png")));
//        orc.setPosition(200, 200);
//        mainStage.addActor(orc);
//
//        gameOverMessage = new ActorBeta();
//        gameOverMessage.setTexture(new Texture(Gdx.files.internal("game-over.png")));
//        gameOverMessage.setPosition(150, 180);
//        gameOverMessage.setVisible(false);
//        mainStage.addActor(gameOverMessage);
//
//        win = false;

        BaseActor grass = new BaseActor(0,0, mainStage);
        grass.loadTexture( "grass.jpg" );
        grass.setSize(800,600);

        new Zoro(400,400, mainStage);
        new Zoro(500,100, mainStage);
        new Zoro(100,450, mainStage);
        new Zoro(200,250, mainStage);

        new Rock(200,150, mainStage);
        new Rock(100,300, mainStage);
        new Rock(300,350, mainStage);
        new Rock(450,200, mainStage);

        mainCharacter = new MainCharacter(20,20, mainStage);
        win = false;
    }

    public void update(float dt)
    {
        for (BaseActor rockActor : BaseActor.getList(mainStage, "Rock"))
            mainCharacter.preventOverlap(rockActor);
        for (BaseActor zoroActor : BaseActor.getList(mainStage, "Zoro"))
        {
            Zoro zoro = (Zoro)zoroActor;
            if ( mainCharacter.overlaps(zoro) && !zoro.collected )
            {
                zoro.collected = true;
                zoro.clearActions();
                zoro.addAction( Actions.fadeOut(1) );
                zoro.addAction( Actions.after( Actions.removeActor() ) );
                Whirlpool whirl = new Whirlpool(0,0, mainStage);
                whirl.centerAtActor( zoro );
                whirl.setOpacity(0.25f);
            }
        }
        if ( BaseActor.count(mainStage, "Zoro") == 0 && !win )
        {
            win = true;
            BaseActor youWinMessage = new BaseActor(0,0,mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(400,300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction( Actions.delay(1) );
            youWinMessage.addAction( Actions.after( Actions.fadeIn(1) ) );
        }
//        // Перевірка, чи персонаж торкнувся зорро
//        if (mainCharacter.overlaps(zoro)) {
//            zoro.remove();
//            winMessage.setVisible(true);
//            win = true;
//        }
//
//        // Перевірка, чи персонаж торкнувся орка
//        if (mainCharacter.overlaps(orc)) {
//            mainCharacter.remove(); // Видалення персонажа зі сцени
//            gameOverMessage.setVisible(true); // Відображення повідомлення "Game Over"
//        }
    }
}
