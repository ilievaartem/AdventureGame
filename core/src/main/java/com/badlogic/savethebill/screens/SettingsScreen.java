package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.GameSettings;

public class SettingsScreen extends BaseScreen {
    private Slider masterVolumeSlider;
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private Label masterVolumeLabel;
    private Label musicVolumeLabel;
    private Label soundVolumeLabel;
    private GameSettings settings;
    private final BaseScreen previousScreen;

    public SettingsScreen(BaseScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    public void initialize() {
        settings = GameSettings.getInstance();

        BaseActor background = new BaseActor(0, 0, mainStage);
        background.loadTexture("Summer6.png");
        background.setSize(mainStage.getViewport().getWorldWidth(), mainStage.getViewport().getWorldHeight());

        Label settingsTitle = new Label("SETTINGS", BaseGame.labelStyle);
        settingsTitle.setColor(Color.WHITE);
        settingsTitle.setFontScale(2.0f);

        Slider.SliderStyle sliderStyle = createSliderStyle();

        masterVolumeLabel = new Label("Total volume: " + Math.round(settings.getMasterVolume() * 100) + "%", BaseGame.labelStyle);
        musicVolumeLabel = new Label("Music: " + Math.round(settings.getMusicVolume() * 100) + "%", BaseGame.labelStyle);
        soundVolumeLabel = new Label("Sound: " + Math.round(settings.getSoundVolume() * 100) + "%", BaseGame.labelStyle);

        masterVolumeSlider = new Slider(0f, 1f, 0.1f, false, sliderStyle);
        masterVolumeSlider.setValue(settings.getMasterVolume());
        masterVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setMasterVolume(masterVolumeSlider.getValue());
                masterVolumeLabel.setText("Total volume: " + Math.round(settings.getMasterVolume() * 100) + "%");
                updateGameSounds();
            }
        });

        musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, sliderStyle);
        musicVolumeSlider.setValue(settings.getMusicVolume());
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setMusicVolume(musicVolumeSlider.getValue());
                musicVolumeLabel.setText("Music: " + Math.round(settings.getMusicVolume() * 100) + "%");
                updateGameSounds();
            }
        });

        soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, sliderStyle);
        soundVolumeSlider.setValue(settings.getSoundVolume());
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setSoundVolume(soundVolumeSlider.getValue());
                soundVolumeLabel.setText("Sound: " + Math.round(settings.getSoundVolume() * 100) + "%");
                updateGameSounds();
            }
        });

        TextButton saveButton = new TextButton("Save", BaseGame.textButtonStyle);
        saveButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            settings.saveSettings();
            if (previousScreen != null) {
                BillGame.setActiveScreen(previousScreen);
            } else {
                BillGame.setActiveScreen(new MenuScreen());
            }
            return true;
        });

        TextButton resetButton = new TextButton("Default", BaseGame.textButtonStyle);
        resetButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            settings.resetToDefaults();
            updateSliders();
            updateGameSounds();
            return true;
        });

        TextButton backButton = new TextButton("Back", BaseGame.textButtonStyle);
        backButton.addListener((Event e) -> {
            if (!(e instanceof InputEvent) || !((InputEvent) e).getType().equals(Type.touchDown))
                return false;
            if (previousScreen != null) {
                BillGame.setActiveScreen(previousScreen);
            } else {
                BillGame.setActiveScreen(new MenuScreen());
            }
            return true;
        });

        uiTable.center();
        uiTable.add(settingsTitle).colspan(3).padBottom(30);
        uiTable.row();

        uiTable.add(masterVolumeLabel).padBottom(10);
        uiTable.row();
        uiTable.add(masterVolumeSlider).width(300).padBottom(20);
        uiTable.row();

        uiTable.add(musicVolumeLabel).padBottom(10);
        uiTable.row();
        uiTable.add(musicVolumeSlider).width(300).padBottom(20);
        uiTable.row();

        uiTable.add(soundVolumeLabel).padBottom(10);
        uiTable.row();
        uiTable.add(soundVolumeSlider).width(300).padBottom(30);
        uiTable.row();

        uiTable.add(saveButton).padRight(10);
        uiTable.add(resetButton).padRight(10);
        uiTable.add(backButton);
    }

    private void updateGameSounds() {
        if (previousScreen instanceof LevelScreen) {
            ((LevelScreen) previousScreen).updateSoundSettings();
        } else if (previousScreen instanceof LevelScreen2) {
            ((LevelScreen2) previousScreen).updateSoundSettings();
        } else if (previousScreen instanceof LevelScreen3) {
            ((LevelScreen3) previousScreen).updateSoundSettings();
        }
    }

    private Slider.SliderStyle createSliderStyle() {
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();

        Texture sliderBg = new Texture(Gdx.files.internal("button.png"));
        NinePatch sliderBgPatch = new NinePatch(sliderBg, 12, 12, 12, 12);
        sliderStyle.background = new NinePatchDrawable(sliderBgPatch);

        Texture knobTex = new Texture(Gdx.files.internal("button.png"));
        NinePatch knobPatch = new NinePatch(knobTex, 12, 12, 12, 12);
        sliderStyle.knob = new NinePatchDrawable(knobPatch);

        return sliderStyle;
    }

    private void updateSliders() {
        masterVolumeSlider.setValue(settings.getMasterVolume());
        musicVolumeSlider.setValue(settings.getMusicVolume());
        soundVolumeSlider.setValue(settings.getSoundVolume());

        masterVolumeLabel.setText("Total volume: " + Math.round(settings.getMasterVolume() * 100) + "%");
        musicVolumeLabel.setText("Music: " + Math.round(settings.getMusicVolume() * 100) + "%");
        soundVolumeLabel.setText("Sound: " + Math.round(settings.getSoundVolume() * 100) + "%");
    }

    public void update(float dt) {
    }

    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.ESCAPE) {
            BillGame.setActiveScreen(previousScreen);
            return true;
        }
        return false;
    }
}
