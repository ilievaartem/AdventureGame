package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class ShopHeart extends BaseActor {
    public ShopHeart(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("heart-icon.png");
    }
}
