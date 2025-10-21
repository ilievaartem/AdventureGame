package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.GameSettings;
import com.badlogic.savethebill.screens.BaseScreen;
import com.badlogic.savethebill.screens.LevelScreen;
import com.badlogic.savethebill.screens.LevelScreen2;
import com.badlogic.savethebill.screens.LevelScreen3;

public class ControlHUD {
    private final Table uiTable;
    private Label npcLabel;
    private boolean isMuted = false;
    private Music levelMusic;
    private static final float INSTRUMENTAL_VOLUME = 0.1f;
    private static final float WIND_VOLUME = 0.1f;
    private static final float LEVEL_MUSIC_VOLUME = 0.1f;
    private static final float EFFECT_VOLUME = 0.1f;
    private final BaseScreen currentScreen;
    private final GameSettings gameSettings;

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass, BaseScreen currentScreen) {
        this(uiStage, screenClass, currentScreen, false);
    }

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass, BaseScreen currentScreen, boolean showNpcLabel) {
        this.currentScreen = currentScreen;
        this.gameSettings = GameSettings.getInstance();
        uiTable = new Table();
        uiTable.setFillParent(true);
        uiTable.top();
        uiStage.addActor(uiTable);

        if (showNpcLabel) {
            npcLabel = new Label("Hostages Left: 0", BaseGame.labelStyle);
            npcLabel.setColor(Color.CYAN);
            uiTable.add(npcLabel).pad(10).expandX();
        } else {
            uiTable.add().pad(10).expandX();
        }
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

    public void setMuted(boolean muted) {
        this.isMuted = muted;
        if (levelMusic != null) {
            levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume());
        }
        notifyMuteStateChanged();
    }

    public void updateSoundSettings() {
        if (levelMusic != null) {
            levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME * gameSettings.getMasterVolume() * gameSettings.getMusicVolume());
        }
        notifyMuteStateChanged();
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
