package com.badlogic.savethebill;

import com.badlogic.savethebill.screens.MenuScreen;

public class BillGame extends BaseGame {
    public void create() {
        setActiveScreen(new MenuScreen());
    }
}
