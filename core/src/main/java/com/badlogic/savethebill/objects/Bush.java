package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Bush extends Solid
{
    public Bush(float x, float y, Stage s)
    {
        super(x,y,48,48,s);
        loadTexture("bush.png");
        setBoundaryPolygon(8);
    }
}
