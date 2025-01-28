package com.badlogic.starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Stage;
public class Rock extends BaseActor{
    public Rock(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("rock-1.png");
        setBoundaryPolygon(8);
    }
}
