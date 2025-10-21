package com.badlogic.savethebill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GameSettings {
    private static GameSettings instance;
    private Preferences prefs;

    private float masterVolume = 1.0f;
    private float musicVolume = 1.0f;
    private float soundVolume = 1.0f;

    private GameSettings() {
        prefs = Gdx.app.getPreferences("game-settings");
        loadSettings();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public void loadSettings() {
        masterVolume = prefs.getFloat("masterVolume", 1.0f);
        musicVolume = prefs.getFloat("musicVolume", 1.0f);
        soundVolume = prefs.getFloat("soundVolume", 1.0f);
    }

    public void saveSettings() {
        prefs.putFloat("masterVolume", masterVolume);
        prefs.putFloat("musicVolume", musicVolume);
        prefs.putFloat("soundVolume", soundVolume);
        prefs.flush();
    }

    public void resetToDefaults() {
        masterVolume = 1.0f;
        musicVolume = 1.0f;
        soundVolume = 1.0f;
        saveSettings();
    }

    public float getMasterVolume() {
        return masterVolume;
    }

    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0f, Math.min(1f, volume));
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0f, Math.min(1f, volume));
    }

    public float getEffectiveMusicVolume() {
        return masterVolume * musicVolume;
    }

    public float getEffectiveSoundVolume() {
        return masterVolume * soundVolume;
    }

    public void applyVolumeToMusic(Music music) {
        if (music != null) {
            music.setVolume(getEffectiveMusicVolume());
        }
    }

    public long playSoundWithVolume(Sound sound) {
        if (sound != null) {
            return sound.play(getEffectiveSoundVolume());
        }
        return -1;
    }
}
