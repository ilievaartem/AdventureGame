package com.cryptotracker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.cryptotracker.screens.CryptoTrackerScreen;

/**
 * Main CryptoTracker application with AML (Anti-Money Laundering) logic
 */
public class CryptoTrackerApp extends Game {
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;
    
    public Skin skin;
    public BitmapFont font;
    
    @Override
    public void create() {
        // Initialize font
        initializeFont();
        
        // Initialize skin
        skin = new Skin();
        
        // Set the main screen
        setScreen(new CryptoTrackerScreen(this));
    }
    
    private void initializeFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
            Gdx.files.internal("OpenSans2.ttf")
        );
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = 18;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 1;
        parameter.borderColor = Color.BLACK;
        parameter.borderStraight = true;
        
        font = generator.generateFont(parameter);
        generator.dispose();
    }
    
    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        super.dispose();
    }
}
