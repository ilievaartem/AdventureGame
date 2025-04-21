package com.badlogic.savethebill.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.savethebill.BaseActor;

public class IcyHeroMovement extends BaseActor {
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;

    private String currentDirection;

    private float normalSpeed = 100;
    private float sprintSpeed = 150;

    public IcyHeroMovement(float x, float y, Stage s) {
        super(x, y, s);

        walkDownAnimation = loadAnimationFromFiles(new String[]{"character-1.png", "character-2.png"}, 0.2f, true);
        walkUpAnimation = loadAnimationFromFiles(new String[]{"character-15.png", "character-16.png"}, 0.2f, true);
        walkLeftAnimation = loadAnimationFromFiles(new String[]{"character-11.png", "character-12.png"}, 0.2f, true);
        walkRightAnimation = loadAnimationFromFiles(new String[]{"character-5.png", "character-6.png"}, 0.2f, true);

        idleDownAnimation = loadAnimationFromFiles(new String[]{"character-3.png"}, 0.2f, false);
        idleUpAnimation = loadAnimationFromFiles(new String[]{"character-13.png"}, 0.2f, false);
        idleLeftAnimation = loadAnimationFromFiles(new String[]{"character-9.png"}, 0.2f, false);
        idleRightAnimation = loadAnimationFromFiles(new String[]{"character-7.png"}, 0.2f, false);

        currentDirection = "down";
        setAnimation(idleDownAnimation);

        setAcceleration(400);
        setMaxSpeed(normalSpeed);
        setDeceleration(400);

        setBoundaryPolygon(8);
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        boolean isWalking = false;

        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
            setMaxSpeed(sprintSpeed);
        } else {
            setMaxSpeed(normalSpeed);
        }

        if (Gdx.input.isKeyPressed(Keys.A)) {
            accelerateAtAngle(180);
            if (!currentDirection.equals("left") || getSpeed() == 0) {
                setAnimation(walkLeftAnimation);
                currentDirection = "left";
            }
            isWalking = true;
        } else if (Gdx.input.isKeyPressed(Keys.D)) {
            accelerateAtAngle(0);
            if (!currentDirection.equals("right") || getSpeed() == 0) {
                setAnimation(walkRightAnimation);
                currentDirection = "right";
            }
            isWalking = true;
        } else if (Gdx.input.isKeyPressed(Keys.W)) {
            accelerateAtAngle(90);
            if (!currentDirection.equals("up") || getSpeed() == 0) {
                setAnimation(walkUpAnimation);
                currentDirection = "up";
            }
            isWalking = true;
        } else if (Gdx.input.isKeyPressed(Keys.S)) {
            accelerateAtAngle(270);
            if (!currentDirection.equals("down") || getSpeed() == 0) {
                setAnimation(walkDownAnimation);
                currentDirection = "down";
            }
            isWalking = true;
        }

        if (!isWalking) {
            switch (currentDirection) {
                case "left":
                    setAnimation(idleLeftAnimation);
                    break;
                case "right":
                    setAnimation(idleRightAnimation);
                    break;
                case "up":
                    setAnimation(idleUpAnimation);
                    break;
                case "down":
                    setAnimation(idleDownAnimation);
                    break;
            }
        }

        applyPhysics(dt);
        boundToWorld();
        alignCamera();
    }

    public float getFacingAngle() {
        switch (currentDirection) {
            case "up":
                return 90;
            case "down":
                return 270;
            case "left":
                return 180;
            case "right":
            default:
                return 0;
        }
    }
}
