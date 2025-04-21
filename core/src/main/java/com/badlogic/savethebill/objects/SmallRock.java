package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class SmallRock extends Solid {
    public SmallRock(float x, float y, Stage s)
    {
        super(x,y,48,48,s);
        loadTexture("rock-3.png");
        setBoundaryPolygon(8);
    }
}
