package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;

public class Coin extends BaseActor {
    public Coin(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("coin.png");
        Action spin = Actions.rotateBy(30, 1);
        this.addAction(Actions.forever(spin));

        setBoundaryPolygon(8);
    }
}
