package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.GameSettings;
import com.badlogic.savethebill.screens.BaseScreen;
import com.badlogic.savethebill.screens.LevelScreen;
import com.badlogic.savethebill.screens.LevelScreen2;
import com.badlogic.savethebill.screens.LevelScreen3;

public class ControlHUD {
    private Table uiTable;
    private Label npcLabel;
    private Button muteButton;
    private Button restartButton;
    private boolean isMuted = false;
    private Music levelMusic;
    private static final float INSTRUMENTAL_VOLUME = 0.1f;
    private static final float WIND_VOLUME = 0.1f;
    private static final float LEVEL_MUSIC_VOLUME = 0.1f;
    private static final float EFFECT_VOLUME = 0.1f;
    private Stage uiStage;
    private BaseScreen currentScreen;
    private GameSettings gameSettings;

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass, BaseScreen currentScreen) {
        this(uiStage, screenClass, currentScreen, false);
    }

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass, BaseScreen currentScreen, boolean showNpcLabel) {
        this.uiStage = uiStage;
        this.currentScreen = currentScreen;
        this.gameSettings = GameSettings.getInstance();
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top();
        uiStage.addActor(uiTable);

        ButtonStyle buttonStyle = new ButtonStyle();
        Texture buttonTex = new Texture(Gdx.files.internal("undo.png"));
        TextureRegion buttonRegion = new TextureRegion(buttonTex);
        buttonStyle.up = new TextureRegionDrawable(buttonRegion);

        restartButton = new Button(buttonStyle);
        restartButton.setColor(Color.CYAN);

        restartButton.addListener(
            (Event e) -> {
                if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                    return false;

                dispose();
                if (currentScreen != null) {
                    currentScreen.dispose();
                }

                try {
                    BaseScreen newScreen = screenClass.getConstructor().newInstance();
                    BillGame.setActiveScreen(newScreen);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        );

        ButtonStyle buttonStyle2 = new ButtonStyle();
        Texture buttonTex2 = new Texture(Gdx.files.internal("audio.png"));
        Texture buttonTex2Muted = new Texture(Gdx.files.internal("no-audio.png"));
        TextureRegion buttonRegion2 = new TextureRegion(buttonTex2);
        TextureRegion buttonRegion2Muted = new TextureRegion(buttonTex2Muted);
        buttonStyle2.up = new TextureRegionDrawable(buttonRegion2);

        muteButton = new Button(buttonStyle2);
        muteButton.setColor(Color.CYAN);

        muteButton.addListener(
            (Event e) -> {
                if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                    return false;

                isMuted = !isMuted;
                muteButton.getStyle().up = isMuted
                    ? new TextureRegionDrawable(buttonRegion2Muted)
                    : new TextureRegionDrawable(buttonRegion2);

                if (levelMusic != null) {
                    levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume());
                }

                notifyMuteStateChanged();

                return true;
            }
        );

        if (showNpcLabel) {
            npcLabel = new Label("Hostages Left: 0", BaseGame.labelStyle);
            npcLabel.setColor(Color.CYAN);
            uiTable.add(npcLabel).pad(10);
        } else {
            uiTable.add().pad(10);
        }

        uiTable.add().expandX();
        uiTable.add(muteButton).top();
        uiTable.add(restartButton).top();
    }

    public void initializeLevelMusic(String musicFile) {
        levelMusic = Gdx.audio.newMusic(Gdx.files.internal(musicFile));
        levelMusic.setLooping(true);
        levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume());
        levelMusic.play();
    }

    public void dispose() {
        if (levelMusic != null) {
            levelMusic.stop();
            levelMusic.dispose();
            levelMusic = null;
        }
    }

    public void updateMuteState(float instrumentalVolume, float windVolume) {
        instrumentalVolume = isMuted ? 0 : INSTRUMENTAL_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume();
        windVolume = isMuted ? 0 : WIND_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume();
        if (levelMusic != null) {
            levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume());
        }
    }

    public void updateNpcLabel(int count) {
        if (npcLabel != null) {
            npcLabel.setText("Hostages Left: " + count);
        }
    }

    public float getInstrumentalVolume() {
        return isMuted ? 0 : INSTRUMENTAL_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume();
    }

    public float getWindVolume() {
        return isMuted ? 0 : WIND_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume();
    }

    public float getEffectVolume() {
        return isMuted ? 0 : EFFECT_VOLUME * gameSettings.getMasterVolume() * gameSettings.getSoundVolume();
    }

    public boolean isMuted() {
        return isMuted;
    }

    private void notifyMuteStateChanged() {
        if (currentScreen != null) {
            if (currentScreen instanceof LevelScreen) {
                ((LevelScreen) currentScreen).updateSoundsMuteState();
            } else if (currentScreen instanceof LevelScreen2) {
                ((LevelScreen2) currentScreen).updateSoundsMuteState();
            } else if (currentScreen instanceof LevelScreen3) {
                ((LevelScreen3) currentScreen).updateSoundsMuteState();
            }
        }
    }
}
