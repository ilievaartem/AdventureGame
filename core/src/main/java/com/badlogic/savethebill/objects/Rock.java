package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class Rock extends BaseActor {
    public Rock(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("rock-1.png");
        setBoundaryPolygon(8);
    }
}
