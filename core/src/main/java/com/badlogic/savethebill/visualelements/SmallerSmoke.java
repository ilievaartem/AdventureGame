package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SmallerSmoke extends BaseActor {
    public SmallerSmoke(float x, float y, Stage s) {
        super(x, y, s);
        loadAnimationFromSheet("smaller_smoke.png", 1, 3, 0.15f, false);
        setSize(32, 32);
        setBoundaryRectangle();
        addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.removeActor()
        ));
    }
}
