package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;

public class Arrow extends BaseActor {
    public Arrow(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("arrow.png");
        setSpeed(400);
    }

    public void act(float dt) {
        super.act(dt);
        applyPhysics(dt);
    }
}
