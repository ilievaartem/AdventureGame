package com.badlogic.savethebill.characters;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.gdx.graphics.g2d.Animation;

public class Spider extends BaseActor {
    private enum State {WALKING, ATTACKING}

    private State state;
    private Animation walkAnimation;
    private Animation jumpAnimation;
    private float attackCooldown;
    private static final float VISION_RADIUS = 200f;
    private static final float ATTACK_COOLDOWN = 2f;
    private static final float WALK_SPEED = 80f;
    private static final float ATTACK_SPEED = 150f;
    private IcyHeroMovement target;

    public Spider(float x, float y, Stage s, IcyHeroMovement target) {
        super(x, y, s);
        this.target = target;

        walkAnimation = loadAnimationFromSheet("spider_walk.png", 1, 4, 0.2f, true);
        jumpAnimation = loadAnimationFromSheet("spider_jump.png", 1, 3, 0.15f, true);
        setAnimation(walkAnimation);

        setSize(32, 48);
        setBoundaryPolygon(6);
        setSpeed(WALK_SPEED);
        setMotionAngle(MathUtils.random(0, 360));
        setMaxSpeed(ATTACK_SPEED);
        setDeceleration(0);

        state = State.WALKING;
        attackCooldown = 0;
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        attackCooldown -= dt;

        float distanceToTarget = getDistanceToTarget();
        if (distanceToTarget <= VISION_RADIUS && attackCooldown <= 0) {
            state = State.ATTACKING;
            setAnimation(jumpAnimation);
            setSpeed(ATTACK_SPEED);
            float angle = calculateAngleToTarget();
            setMotionAngle(angle);
        } else if (state == State.ATTACKING && distanceToTarget > VISION_RADIUS) {
            state = State.WALKING;
            setAnimation(walkAnimation);
            setSpeed(WALK_SPEED);
            setMotionAngle(MathUtils.random(0, 360));
        }

        if (state == State.WALKING) {
            if (MathUtils.random(1, 120) == 1) {
                setMotionAngle(MathUtils.random(0, 360));
            }
        } else if (state == State.ATTACKING) {
            float angle = calculateAngleToTarget();
            setMotionAngle(angle);
        }

        applyPhysics(dt);
        boundToWorld();
    }

    private float getDistanceToTarget() {
        if (target == null) return Float.MAX_VALUE;
        float dx = target.getX() + target.getWidth() / 2 - (getX() + getWidth() / 2);
        float dy = target.getY() + target.getHeight() / 2 - (getY() + getHeight() / 2);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float calculateAngleToTarget() {
        if (target == null) return 0;
        float dx = target.getX() + target.getWidth() / 2 - (getX() + getWidth() / 2);
        float dy = target.getY() + target.getHeight() / 2 - (getY() + getHeight() / 2);
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public boolean isAttacking() {
        return state == State.ATTACKING;
    }

    public void resetAttackCooldown() {
        attackCooldown = ATTACK_COOLDOWN;
    }
}
