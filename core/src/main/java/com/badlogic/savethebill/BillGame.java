package com.badlogic.savethebill;

import com.badlogic.gdx.Gdx;
import com.badlogic.savethebill.screens.MenuScreen;

public class BillGame extends BaseGame {
    public void create() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        super.create();
        setActiveScreen(new MenuScreen());
    }
}
