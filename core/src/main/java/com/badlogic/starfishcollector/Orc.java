package com.badlogic.starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Orc extends BaseActor {
    public Orc(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("orc-1.png");
        setBoundaryPolygon(8);
    }
}
