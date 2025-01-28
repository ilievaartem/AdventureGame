package com.badlogic.starfishcollector;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
public class Zoro extends BaseActor
{
    public boolean collected;
    public Zoro(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("zoro-1.png");
        Action spin = Actions.rotateBy(30, 1);
        this.addAction( Actions.forever(spin) );

        setBoundaryPolygon(8);

        collected = false;
    }

//    public boolean isCollected()
//    {
//        return collected;
//    }
//    public void collect()
//    {
//        collected = true;
//        clearActions();
//        addAction( Actions.fadeOut(1) );
//        addAction( Actions.after( Actions.removeActor() ) );
//    }
}
