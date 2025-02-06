package com.badlogic.savethebill;

import com.badlogic.gdx.Game;
import com.badlogic.savethebill.screens.BaseScreen;

public abstract class BaseGame extends Game
{
    private static BaseGame game;
    public BaseGame()
    {
        game = this;
    }
    public static void setActiveScreen(BaseScreen s)
    {
        game.setScreen(s);
    }
}
