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
import com.badlogic.savethebill.SaveManager;

public class PauseScreen extends BaseScreen {
    private final BaseScreen gameScreen;
    private final Class<? extends BaseScreen> gameScreenClass;
    private TextButton muteButton;
    private GameSettings gameSettings;
    private SaveManager saveManager;
    private boolean showRestartHint = false;
    private Label restartHintLabel;
    private boolean showingExitConfirmation = false;
    private boolean hasUnsavedProgress = true;

    public PauseScreen(BaseScreen gameScreen, Class<? extends BaseScreen> gameScreenClass, boolean isMuted) {
        this.gameScreen = gameScreen;
        this.gameScreenClass = gameScreenClass;
        this.saveManager = SaveManager.getInstance();
    }

    public void initialize() {
        this.gameSettings = GameSettings.getInstance();

        if (showingExitConfirmation) {
            initializeExitConfirmation();
        } else {
            initializeMainMenu();
        }
    }

    private void initializeMainMenu() {
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

        TextButton saveButton = new TextButton("Save Progress", buttonStyle);
        saveButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            saveGameProgress();
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
            checkAndExitToMenu();
            return true;
        });

        uiTable.center();
        uiTable.add(pauseTitle).padBottom(40);
        uiTable.row();
        uiTable.add(continueButton).width(250).height(60).padBottom(15);
        uiTable.row();
        uiTable.add(saveButton).width(250).height(60).padBottom(15);
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

    private void initializeExitConfirmation() {
        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("Summer6.png");
        background.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());
        background.setColor(0.3f, 0.3f, 0.3f, 0.9f);

        Label confirmTitle = new Label("Exit Confirmation", BaseGame.labelStyle);
        confirmTitle.setColor(Color.WHITE);
        confirmTitle.setFontScale(1.5f);

        String warningText = hasUnsavedProgress ?
            "Are you sure you want to exit without saving?" :
            "Are you sure you want to exit to main menu?";

        Label warningLabel = new Label(warningText, BaseGame.labelStyle);
        warningLabel.setColor(Color.YELLOW);
        warningLabel.setFontScale(1.0f);

        TextButton.TextButtonStyle buttonStyle = createButtonStyle();

        TextButton yesButton = new TextButton("Yes", buttonStyle);
        yesButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            exitToMenu();
            return true;
        });

        TextButton noButton = new TextButton("No", buttonStyle);
        noButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            showingExitConfirmation = false;
            BillGame.setActiveScreen(new PauseScreen(gameScreen, gameScreenClass, gameSettings.isMuted()));
            return true;
        });

        uiTable.center();
        uiTable.add(confirmTitle).padBottom(30);
        uiTable.row();
        uiTable.add(warningLabel).padBottom(40);
        uiTable.row();
        uiTable.add(yesButton).width(150).height(60).padRight(20);
        uiTable.add(noButton).width(150).height(60).padLeft(20);
    }

    private void saveGameProgress() {
        int currentLevel = getCurrentLevelNumber();
        int health = getCurrentHealth();
        int coins = getCurrentCoins();
        int arrows = getCurrentArrows();
        String destroyedObjects = getCurrentDestroyedObjects();
        boolean treasureOpened = getCurrentTreasureOpened();

        saveManager.saveGameWithState(currentLevel, health, coins, arrows, destroyedObjects, treasureOpened);
        hasUnsavedProgress = false;

        uiTable.clear();
        initializeMainMenu();

        Label saveConfirmLabel = new Label("Game Saved!", BaseGame.labelStyle);
        saveConfirmLabel.setColor(Color.GREEN);
        saveConfirmLabel.setFontScale(1.2f);
        uiTable.row();
        uiTable.add(saveConfirmLabel).padTop(20);
    }

    private void checkAndExitToMenu() {
        if (hasUnsavedProgress) {
            showingExitConfirmation = true;
            uiTable.clear();
            initializeExitConfirmation();
        } else {
            exitToMenu();
        }
    }

    private int getCurrentLevelNumber() {
        if (gameScreen instanceof LevelScreen) {
            return 1;
        } else if (gameScreen instanceof LevelScreen2) {
            return 2;
        } else if (gameScreen instanceof LevelScreen3) {
            return 3;
        }
        return 1;
    }

    private int getCurrentHealth() {
        if (gameScreen instanceof LevelScreen) {
            return ((LevelScreen) gameScreen).getHealth();
        } else if (gameScreen instanceof LevelScreen2) {
            return ((LevelScreen2) gameScreen).getHealth();
        } else if (gameScreen instanceof LevelScreen3) {
            return ((LevelScreen3) gameScreen).getHealth();
        }
        return 3;
    }

    private int getCurrentCoins() {
        if (gameScreen instanceof LevelScreen) {
            return ((LevelScreen) gameScreen).getCoins();
        } else if (gameScreen instanceof LevelScreen2) {
            return ((LevelScreen2) gameScreen).getCoins();
        } else if (gameScreen instanceof LevelScreen3) {
            return ((LevelScreen3) gameScreen).getCoins();
        }
        return 0;
    }

    private int getCurrentArrows() {
        if (gameScreen instanceof LevelScreen) {
            return ((LevelScreen) gameScreen).getArrows();
        } else if (gameScreen instanceof LevelScreen2) {
            return ((LevelScreen2) gameScreen).getArrows();
        } else if (gameScreen instanceof LevelScreen3) {
            return ((LevelScreen3) gameScreen).getArrows();
        }
        return 0;
    }

    private String getCurrentDestroyedObjects() {
        if (gameScreen instanceof LevelScreen) {
            return ((LevelScreen) gameScreen).getDestroyedObjects();
        } else if (gameScreen instanceof LevelScreen2) {
            return ((LevelScreen2) gameScreen).getDestroyedObjects();
        } else if (gameScreen instanceof LevelScreen3) {
            return ((LevelScreen3) gameScreen).getDestroyedObjects();
        }
        return "";
    }

    private boolean getCurrentTreasureOpened() {
        if (gameScreen instanceof LevelScreen) {
            return ((LevelScreen) gameScreen).isTreasureOpened();
        } else if (gameScreen instanceof LevelScreen2) {
            return ((LevelScreen2) gameScreen).isTreasureOpened();
        } else if (gameScreen instanceof LevelScreen3) {
            return ((LevelScreen3) gameScreen).isTreasureOpened();
        }
        return false;
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
            if (showingExitConfirmation) {
                showingExitConfirmation = false;
                BillGame.setActiveScreen(new PauseScreen(gameScreen, gameScreenClass, gameSettings.isMuted()));
            } else {
                resumeGame();
            }
            return true;
        }
        return false;
    }

    @Override
    public void render(float delta) {
        if (!showingExitConfirmation && muteButton != null) {
            boolean currentMuteState = gameSettings.isMuted();
            String expectedText = currentMuteState ? "Unmute" : "Mute";

            if (!muteButton.getText().toString().equals(expectedText)) {
                muteButton.setText(expectedText);
            }
        }

        super.render(delta);
    }
}
