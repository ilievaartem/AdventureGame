package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Zoro extends NPC {
    public boolean collected;

    public Zoro(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("zoro-1.png");
        setBoundaryRectangle();
    }
}
