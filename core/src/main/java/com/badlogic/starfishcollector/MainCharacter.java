package com.badlogic.starfishcollector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainCharacter extends BaseActor {
    public MainCharacter(float x, float y, Stage s) {
        super(x, y, s);
        String[] filenames =
            {"character-1.png", "character-2.png", "character-3.png", "character-7.png",
                "character-5.png", "character-6.png", "character-9.png",
                "character-11.png", "character-12.png", "character-13.png"};
        loadAnimationFromFiles(filenames, 0.1f, true);

        setAcceleration(400);
        setMaxSpeed(100);
        setDeceleration(400);

        setBoundaryPolygon(8);
    }

    public void act(float dt) {
        super.act(dt);
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            accelerateAtAngle(180);
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            accelerateAtAngle(0);
        if (Gdx.input.isKeyPressed(Keys.UP))
            accelerateAtAngle(90);
        if (Gdx.input.isKeyPressed(Keys.DOWN))
            accelerateAtAngle(270);
        applyPhysics(dt);
        setAnimationPaused(!isMoving());
        if (getSpeed() > 0)
            setRotation(getMotionAngle());

        boundToWorld();

        alignCamera();
    }
}
