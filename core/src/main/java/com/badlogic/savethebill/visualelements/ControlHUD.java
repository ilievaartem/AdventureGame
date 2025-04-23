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
import com.badlogic.savethebill.screens.BaseScreen;

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

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass) {
        this(uiStage, screenClass, false);
    }

    public ControlHUD(Stage uiStage, Class<? extends BaseScreen> screenClass, boolean showNpcLabel) {
        this.uiStage = uiStage;
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
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                    return false;

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
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(InputEvent.Type.touchDown))
                    return false;

                isMuted = !isMuted;
                muteButton.getStyle().up = isMuted
                    ? new TextureRegionDrawable(buttonRegion2Muted)
                    : new TextureRegionDrawable(buttonRegion2);

                if (levelMusic != null) {
                    levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME);
                }

                return true;
            }
        );

        if (showNpcLabel) {
            npcLabel = new Label("Zoro's Left: 0", BaseGame.labelStyle);
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
        levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME);
        levelMusic.play();
    }

    public void dispose() {
        if (levelMusic != null) {
            levelMusic.stop();
            levelMusic.dispose();
        }
    }

    public void updateMuteState(float instrumentalVolume, float windVolume) {
        instrumentalVolume = isMuted ? 0 : INSTRUMENTAL_VOLUME;
        windVolume = isMuted ? 0 : WIND_VOLUME;
        if (levelMusic != null) {
            levelMusic.setVolume(isMuted ? 0 : LEVEL_MUSIC_VOLUME);
        }
    }

    public void updateNpcLabel(int count) {
        if (npcLabel != null) {
            npcLabel.setText("Zoro's Left: " + count);
        }
    }

    public float getInstrumentalVolume() {
        return isMuted ? 0 : INSTRUMENTAL_VOLUME;
    }

    public float getWindVolume() {
        return isMuted ? 0 : WIND_VOLUME;
    }

    public float getEffectVolume() {
        return isMuted ? 0 : EFFECT_VOLUME;
    }

    public boolean isMuted() {
        return isMuted;
    }
}
