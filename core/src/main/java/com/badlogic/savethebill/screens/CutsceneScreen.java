package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.savethebill.BillGame;

public class CutsceneScreen extends BaseScreen {
    private Label storyLabel;
    private String[] storyText;
    private int currentTextIndex;
    private String currentText;
    private float typingTimer;
    private float typingDelay = 0.05f;
    private int charIndex;
    private boolean isTyping;
    private float switchDelayTimer;
    private float switchDelay = 2.0f;

    public void initialize() {
        Gdx.gl.glClearColor(0, 0, 0, 1);

        storyText = new String[]{
            "Once, the thriving village of Peaceful stood amidst green meadows...",
            "But one day, darkness came and the village was destroyed by enemies.",
            "The people were kidnapped, leaving only ashes and despair...",
            "Yet hope remains and only one brave hero can save them all!"
        };
        currentTextIndex = 0;
        currentText = "";
        charIndex = 0;
        typingTimer = 0;
        isTyping = true;
        switchDelayTimer = 0;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans2.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        parameter.color = Color.WHITE;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        storyLabel = new Label("", labelStyle);
        storyLabel.setFontScale(1.5f);
        storyLabel.setWrap(true);
        storyLabel.setWidth(mainStage.getViewport().getWorldWidth() - 100);
        storyLabel.setPosition(50, mainStage.getViewport().getWorldHeight() / 2 - 50);

        mainStage.addActor(storyLabel);
    }

    public void update(float dt) {
        if (isTyping) {
            typingTimer += dt;
            if (typingTimer >= typingDelay) {
                typingTimer = 0;
                if (charIndex < storyText[currentTextIndex].length()) {
                    currentText += storyText[currentTextIndex].charAt(charIndex);
                    charIndex++;
                    storyLabel.setText(currentText);
                } else {
                    isTyping = false;
                    switchDelayTimer = 0;
                }
            }
        } else {
            switchDelayTimer += dt;
            if (switchDelayTimer >= switchDelay) {
                currentTextIndex++;
                if (currentTextIndex >= storyText.length) {
                    BillGame.setActiveScreen(new LevelScreen());
                } else {
                    currentText = "";
                    charIndex = 0;
                    isTyping = true;
                    storyLabel.setText(currentText);
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            BillGame.setActiveScreen(new LevelScreen());
        }
    }

    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.SPACE) {
            BillGame.setActiveScreen(new LevelScreen());
            return true;
        }
        if (keyCode == Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }
}
