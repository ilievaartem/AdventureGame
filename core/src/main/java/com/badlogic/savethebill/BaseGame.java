package com.badlogic.savethebill;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.savethebill.screens.BaseScreen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public abstract class BaseGame extends Game
{
    private static BaseGame game;
    public BaseGame()
    {
        game = this;
    }
    public static LabelStyle labelStyle;
    public void create()
    {
        InputMultiplexer im = new InputMultiplexer();
        Gdx.input.setInputProcessor( im );

        labelStyle = new LabelStyle();
        labelStyle.font = new BitmapFont();
    }
    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);
    }
}
