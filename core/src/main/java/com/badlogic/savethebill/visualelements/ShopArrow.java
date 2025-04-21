package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class ShopArrow extends BaseActor {
    public ShopArrow(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("arrow-icon.png");
    }
}
