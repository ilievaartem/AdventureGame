package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;

public class MenuScreen extends BaseScreen {
    public void initialize() {
        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("Summer5.png");
        grass.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());
        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTexture("game-name.png");

        TextButton startButton = new TextButton("Start", BaseGame.textButtonStyle);

        startButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                BillGame.setActiveScreen(new LevelScreen3());
                return false;
            }
        );

        TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);

        quitButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                Gdx.app.exit();
                return false;
            }
        );

        uiTable.add(title).colspan(2);
        uiTable.row();
        uiTable.add(startButton);
        uiTable.add(quitButton);
    }

    public void update(float dt) {
//        if (Gdx.input.isKeyPressed(Keys.S))
//            BillGame.setActiveScreen(new LevelScreen2());
    }

    public boolean keyDown(int keyCode)
    {
        if (Gdx.input.isKeyPressed(Keys.ENTER))
            BillGame.setActiveScreen( new LevelScreen3() );
        if (Gdx.input.isKeyPressed(Keys.ESCAPE))
            Gdx.app.exit();
        return false;
    }
}
