package com.badlogic.savethebill.characters;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.savethebill.BaseActor;

public class Flyer extends BaseActor
{
    private int health;
    private static final int MAX_HEALTH = 1;

    public Flyer(float x, float y, Stage s)
    {
        super(x,y,s);
        loadAnimationFromSheet( "enemy-flyer.png", 1, 5, 0.3f, true);
        setSize(48,48);
        setBoundaryPolygon(6);
        setSpeed( MathUtils.random(50,80) );
        setMotionAngle( MathUtils.random(0,360) );
        this.health = MAX_HEALTH;
    }

    public void act(float dt)
    {
        super.act(dt);
        if ( MathUtils.random(1,120) == 1 )
            setMotionAngle( MathUtils.random(0,360) );
        applyPhysics(dt);
        boundToWorld();
    }

    public void takeDamage(int damage, String damageType) {
        if (isDead()) return;

        int actualDamage = damage;

        health -= actualDamage;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }
}
