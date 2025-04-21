package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class Treasure extends BaseActor {
    public Treasure(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("treasure-chest.png");
    }
}
