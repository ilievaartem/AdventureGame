package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class Coin extends BaseActor
{
    public Coin(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("coin.png");
    }
}
