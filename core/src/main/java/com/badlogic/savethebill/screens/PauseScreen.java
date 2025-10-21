package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;

public class PauseScreen extends BaseScreen {
    private final BaseScreen gameScreen;
    private final Class<? extends BaseScreen> gameScreenClass;
    private boolean isMuted;
    private TextButton muteButton;

    public PauseScreen(BaseScreen gameScreen, Class<? extends BaseScreen> gameScreenClass, boolean isMuted) {
        this.gameScreen = gameScreen;
        this.gameScreenClass = gameScreenClass;
        this.isMuted = isMuted;
    }

    public void initialize() {
        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("Summer6.png");
        background.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());
        background.setColor(0.3f, 0.3f, 0.3f, 0.8f);

        Label pauseTitle = new Label("PAUSE", BaseGame.labelStyle);
        pauseTitle.setColor(Color.WHITE);
        pauseTitle.setFontScale(2.0f);

        TextButton.TextButtonStyle buttonStyle = createButtonStyle();

        TextButton continueButton = new TextButton("Continue", buttonStyle);
        continueButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            resumeGame();
            return true;
        });

        TextButton settingsButton = new TextButton("Settings", buttonStyle);
        settingsButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            BillGame.setActiveScreen(new SettingsScreen(this));
            return true;
        });

        TextButton restartButton = new TextButton("Restart Level", buttonStyle);
        restartButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            restartLevel();
            return true;
        });

        String muteText = isMuted ? "Unmute" : "Mute";
        muteButton = new TextButton(muteText, buttonStyle);
        muteButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            toggleMute();
            return true;
        });

        TextButton exitButton = new TextButton("Exit to Menu", buttonStyle);
        exitButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            exitToMenu();
            return true;
        });

        uiTable.center();
        uiTable.add(pauseTitle).padBottom(40);
        uiTable.row();
        uiTable.add(continueButton).width(250).height(60).padBottom(15);
        uiTable.row();
        uiTable.add(settingsButton).width(250).height(60).padBottom(15);
        uiTable.row();
        uiTable.add(restartButton).width(250).height(60).padBottom(15);
        uiTable.row();
        uiTable.add(muteButton).width(250).height(60).padBottom(15);
        uiTable.row();
        uiTable.add(exitButton).width(250).height(60);
    }

    private TextButton.TextButtonStyle createButtonStyle() {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = BaseGame.labelStyle.font;
        buttonStyle.fontColor = Color.WHITE;

        Texture buttonTex = new Texture(Gdx.files.internal("button.png"));
        NinePatch buttonPatch = new NinePatch(buttonTex, 12, 12, 12, 12);
        buttonStyle.up = new NinePatchDrawable(buttonPatch);
        buttonStyle.down = new NinePatchDrawable(buttonPatch);

        return buttonStyle;
    }

    private void resumeGame() {
        BillGame.setActiveScreen(gameScreen);
    }

    private void restartLevel() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        try {
            BaseScreen newScreen = gameScreenClass.getConstructor().newInstance();
            BillGame.setActiveScreen(newScreen);
        } catch (Exception ex) {
            ex.printStackTrace();
            BillGame.setActiveScreen(new MenuScreen());
        }
    }

    private void toggleMute() {
        isMuted = !isMuted;
        muteButton.setText(isMuted ? "Unmute" : "Mute");

        if (gameScreen instanceof LevelScreen) {
            ((LevelScreen) gameScreen).setMuted(isMuted);
        } else if (gameScreen instanceof LevelScreen2) {
            ((LevelScreen2) gameScreen).setMuted(isMuted);
        } else if (gameScreen instanceof LevelScreen3) {
            ((LevelScreen3) gameScreen).setMuted(isMuted);
        }
    }

    private void exitToMenu() {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        BillGame.setActiveScreen(new MenuScreen());
    }

    public void update(float dt) {
    }

    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.ESCAPE || keyCode == Keys.P) {
            resumeGame();
            return true;
        }
        return false;
    }

    @Override
    public void render(float delta) {
        boolean currentMuteState = false;
        if (gameScreen instanceof LevelScreen) {
            currentMuteState = ((LevelScreen) gameScreen).getControlHUD().isMuted();
        } else if (gameScreen instanceof LevelScreen2) {
            currentMuteState = ((LevelScreen2) gameScreen).getControlHUD().isMuted();
        } else if (gameScreen instanceof LevelScreen3) {
            currentMuteState = ((LevelScreen3) gameScreen).getControlHUD().isMuted();
        }

        if (currentMuteState != isMuted) {
            isMuted = currentMuteState;
            muteButton.setText(isMuted ? "Unmute" : "Mute");
        }

        super.render(delta);
    }
}
