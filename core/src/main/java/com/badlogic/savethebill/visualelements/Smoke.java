package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;

public class Smoke extends BaseActor {
    public Smoke(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("smoke.png");
        addAction(Actions.fadeOut(0.5f));
        addAction(Actions.after(Actions.removeActor()));
    }
}
