package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.savethebill.BaseActor;

public class Flyer extends BaseActor
{
    public Flyer(float x, float y, Stage s)
    {
        super(x,y,s);
        loadAnimationFromSheet( "enemy-flyer.png", 1, 5, 0.3f, true);
        setSize(48,48);
        setBoundaryPolygon(6);
        setSpeed( MathUtils.random(50,80) );
        setMotionAngle( MathUtils.random(0,360) );
    }
    public void act(float dt)
    {
        super.act(dt);
        if ( MathUtils.random(1,120) == 1 )
            setMotionAngle( MathUtils.random(0,360) );
        applyPhysics(dt);
        boundToWorld();
    }
}
