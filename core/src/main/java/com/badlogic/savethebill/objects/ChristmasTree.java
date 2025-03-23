package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class ChristmasTree extends BaseActor {
    public ChristmasTree(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("christmas-tree.png");
        setBoundaryRectangle();
    }
}
