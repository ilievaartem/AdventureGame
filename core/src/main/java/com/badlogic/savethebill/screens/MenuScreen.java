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
                BillGame.setActiveScreen(new CutsceneScreen());
                return true;
            }
        );

        TextButton settingsButton = new TextButton("Settings", BaseGame.textButtonStyle);

        settingsButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                BillGame.setActiveScreen(new SettingsScreen(this));
                return true;
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
                return true;
            }
        );

        uiTable.add(title).colspan(3);
        uiTable.row();
        uiTable.add(startButton);
        uiTable.add(settingsButton);
        uiTable.add(quitButton);
    }

    public void update(float dt) {
    }

    public boolean keyDown(int keyCode) {
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            BillGame.setActiveScreen(new CutsceneScreen());
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }
}
