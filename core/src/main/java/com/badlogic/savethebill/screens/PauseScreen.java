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
import com.badlogic.savethebill.GameSettings;

public class PauseScreen extends BaseScreen {
    private final BaseScreen gameScreen;
    private final Class<? extends BaseScreen> gameScreenClass;
    private TextButton muteButton;
    private GameSettings gameSettings;
    private boolean showRestartHint = false;
    private Label restartHintLabel;

    public PauseScreen(BaseScreen gameScreen, Class<? extends BaseScreen> gameScreenClass, boolean isMuted) {
        this.gameScreen = gameScreen;
        this.gameScreenClass = gameScreenClass;
    }

    public void initialize() {
        this.gameSettings = GameSettings.getInstance();

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
            showSoundRestartHint();
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

        String muteText = gameSettings.isMuted() ? "Unmute" : "Mute";
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

        if (showRestartHint) {
            restartHintLabel = new Label("Note: Restart level to fully apply sound changes", BaseGame.labelStyle);
            restartHintLabel.setColor(Color.YELLOW);
            restartHintLabel.setFontScale(0.7f);
            uiTable.row();
            uiTable.add(restartHintLabel).padTop(20);
        }
    }

    public void showSoundRestartHint() {
        this.showRestartHint = true;
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
        boolean newMuteState = !gameSettings.isMuted();
        gameSettings.setMuted(newMuteState);
        gameSettings.saveSettings();

        muteButton.setText(newMuteState ? "Unmute" : "Mute");

        if (gameScreen instanceof LevelScreen) {
            ((LevelScreen) gameScreen).setMuted(newMuteState);
        } else if (gameScreen instanceof LevelScreen2) {
            ((LevelScreen2) gameScreen).setMuted(newMuteState);
        } else if (gameScreen instanceof LevelScreen3) {
            ((LevelScreen3) gameScreen).setMuted(newMuteState);
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
        boolean currentMuteState = gameSettings.isMuted();
        String expectedText = currentMuteState ? "Unmute" : "Mute";

        if (!muteButton.getText().toString().equals(expectedText)) {
            muteButton.setText(expectedText);
        }

        super.render(delta);
    }
}
