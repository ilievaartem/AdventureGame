package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.SaveManager;

public class MenuScreen extends BaseScreen {
    private TextButton continueButton;
    private SaveManager saveManager;

    public void initialize() {
        saveManager = SaveManager.getInstance();

        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("Summer5.png");
        grass.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());
        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTexture("game-name.png");

        TextButton newGameButton = new TextButton("New Game", BaseGame.textButtonStyle);
        newGameButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                // Clear any existing save when starting new game
                saveManager.deleteSave();
                BillGame.setActiveScreen(new CutsceneScreen());
                return true;
            }
        );

        continueButton = new TextButton("Continue", BaseGame.textButtonStyle);
        continueButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;
                loadSavedGame();
                return true;
            }
        );

        updateContinueButtonStyle();

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

        uiTable.add(title).colspan(4);
        uiTable.row();
        uiTable.add(newGameButton);
        uiTable.add(continueButton);
        uiTable.add(settingsButton);
        uiTable.add(quitButton);
    }

    private void updateContinueButtonStyle() {
        if (!saveManager.hasSavedGame()) {
            continueButton.setDisabled(true);
            continueButton.getStyle().fontColor = Color.GRAY;
        } else {
            continueButton.setDisabled(false);
            continueButton.getStyle().fontColor = Color.WHITE;
        }
    }

    private void loadSavedGame() {
        if (saveManager.hasSavedGame()) {
            SaveManager.GameSaveData saveData = saveManager.loadGame();
            BaseScreen levelScreen = saveManager.createLevelScreen(saveData);
            BillGame.setActiveScreen(levelScreen);
        }
    }

    public void update(float dt) {
        updateContinueButtonStyle();
    }

    public boolean keyDown(int keyCode) {
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            if (saveManager.hasSavedGame()) {
                loadSavedGame();
            } else {
                saveManager.deleteSave();
                BillGame.setActiveScreen(new CutsceneScreen());
            }
            return true;
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }
}
