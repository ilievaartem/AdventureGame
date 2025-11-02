package com.badlogic.savethebill.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.visualelements.SmallerSmoke;
import com.badlogic.savethebill.visualelements.ControlHUD;

public class Orc extends BaseActor {
    private enum State {
        WALKING, ATTACKING, DYING, DEAD
    }

    private State state;
    private Hero target;
    private int health;
    protected float walkSpeed;
    protected float visionRadius;
    protected float attackRadius;
    private float attackCooldown;
    private float attackTimer;
    private float patrolTimer;
    private float animationTime;
    private float[] attackFrameWidths = {64, 64, 96, 88, 79};
    private float[] attackFrameHeights = {53, 77, 80, 63, 63};
    private float[] dyingFrameWidths = {64, 78, 86, 93};
    private float[] dyingFrameHeights = {53, 52, 52, 31};
    private boolean facingRight = true;
    private Sound hitSound;
    private Sound dieSound;
    private ControlHUD controlHUD;

    private Animation<TextureRegion> walkingAnimation;
    private Animation<TextureRegion> attackingAnimation;
    private Animation<TextureRegion> dyingAnimation;

    public Orc(float x, float y, Stage s, Hero target, ControlHUD controlHUD) {
        super(x, y, s);
        this.target = target;
        this.state = State.WALKING;
        this.health = 5;
        this.walkSpeed = 50f;
        this.visionRadius = 300f;
        this.attackRadius = 80f;
        this.attackCooldown = 2.0f;
        this.attackTimer = 0f;
        this.patrolTimer = 0f;
        this.animationTime = 0f;
        this.controlHUD = controlHUD;

        walkingAnimation = loadAnimationFromSheet("orc_walking.png", 1, 10, 0.08f, true);
        attackingAnimation = loadAnimationFromSheet("orc_attacking.png", 1, 5, 0.5f, false);
        dyingAnimation = loadAnimationFromSheet("orc_dying.png", 1, 4, 0.3f, false);

        setAnimation(walkingAnimation);

        setSize(64, 53);
        setBoundaryRectangle();
        setSpeed(walkSpeed);
        setMotionAngle(MathUtils.random(0, 360));

        hitSound = Gdx.audio.newSound(Gdx.files.internal("Orc_Hit.ogg"));
        dieSound = Gdx.audio.newSound(Gdx.files.internal("Orc_Die.ogg"));
    }

    public void act(float dt) {
        super.act(dt);
        if (state == State.DEAD) return;

        animationTime += dt;
        attackTimer += dt;
        patrolTimer += dt;

        applyPhysics(dt);

        for (BaseActor rock : BaseActor.getList(getStage(), "com.badlogic.savethebill.objects.Rock")) {
            if (overlaps(rock)) {
                preventOverlap(rock);
                setMotionAngle(getMotionAngle() + 180);
            }
        }

        updateFacingDirection();

        switch (state) {
            case WALKING:
                updateWalking(dt);
                break;
            case ATTACKING:
                if (animationTime >= 0.5f * 5) {
                    state = State.WALKING;
                    setAnimation(walkingAnimation);
                    setSize(64, 53);
                    setBoundaryRectangle();
                    setSpeed(walkSpeed);
                }
                break;
            case DYING:
                if (animationTime >= 0.3f * 4) {
                    state = State.DEAD;
                    remove();
                    new SmallerSmoke(getX(), getY(), getStage());
                }
                break;
        }

        boundToWorld();
    }

    private void updateWalking(float dt) {
        float distanceToTarget = distanceTo(target);

        if (distanceToTarget <= visionRadius) {
            Vector2 direction = new Vector2(target.getX() - getX(), target.getY() - getY());
            setMotionAngle(direction.angle());
            setSpeed(walkSpeed);

            if (distanceToTarget <= attackRadius && attackTimer >= attackCooldown) {
                startAttack();
            }
        } else {
            setSpeed(walkSpeed);
            if (patrolTimer >= 3.0f) {
                setMotionAngle(MathUtils.random(0, 360));
                patrolTimer = 0f;
            }
        }

        if (state == State.WALKING && getAnimation() != walkingAnimation) {
            setAnimation(walkingAnimation);
        }
    }

    private void startAttack() {
        state = State.ATTACKING;
        attackTimer = 0f;
        animationTime = 0f;
        setSpeed(0);

        clearActions();

        System.out.println("Starting attack, loading orc_attacking.png");

        setAnimation(attackingAnimation);
        setScale(facingRight ? 1 : -1, 1);

        addAction(Actions.sequence(
            Actions.run(() -> setSize(attackFrameWidths[0], attackFrameHeights[0])),
            Actions.delay(0.5f),
            Actions.run(() -> setSize(attackFrameWidths[1], attackFrameHeights[1])),
            Actions.delay(0.5f),
            Actions.run(() -> setSize(attackFrameWidths[2], attackFrameHeights[2])),
            Actions.delay(0.5f),
            Actions.run(() -> setSize(attackFrameWidths[3], attackFrameHeights[3])),
            Actions.delay(0.5f),
            Actions.run(() -> setSize(attackFrameWidths[4], attackFrameHeights[4])),
            Actions.delay(0.5f)
        ));
    }

    public void takeDamage(int damage, String damageType) {
        if (state == State.DEAD || state == State.DYING) return;

        int actualDamage = damage;

        if (damageType.equals("sword")) {
            actualDamage = 2;
        } else if (damageType.equals("arrow")) {
            actualDamage = MathUtils.random(1, 3);
        }

        health -= actualDamage;

        clearActions();
        addAction(Actions.sequence(
            Actions.color(Color.RED, 0.2f),
            Actions.color(Color.WHITE, 0.2f)
        ));

        if (hitSound != null && controlHUD != null && !controlHUD.isMuted()) {
            System.out.println("Playing Orc_Hit sound at volume: " + controlHUD.getEffectVolume());
            hitSound.play(controlHUD.getEffectVolume());
        } else {
            System.out.println("Cannot play Orc_Hit sound: " +
                "hitSound=" + (hitSound != null) + ", controlHUD=" + (controlHUD != null) +
                ", isMuted=" + (controlHUD != null ? controlHUD.isMuted() : "N/A"));
        }

        if (health <= 0) {
            startDying();
        }
    }

    private void startDying() {
        state = State.DYING;
        animationTime = 0f;
        setSpeed(0);

        clearActions();

        System.out.println("Starting dying, loading orc_dying.png");

        setAnimation(dyingAnimation);
        setScale(facingRight ? 1 : -1, 1);

        addAction(Actions.sequence(
            Actions.run(() -> setSize(dyingFrameWidths[0], dyingFrameHeights[0])),
            Actions.delay(0.3f),
            Actions.run(() -> setSize(dyingFrameWidths[1], dyingFrameHeights[1])),
            Actions.delay(0.3f),
            Actions.run(() -> setSize(dyingFrameWidths[2], dyingFrameHeights[2])),
            Actions.delay(0.3f),
            Actions.run(() -> setSize(dyingFrameWidths[3], dyingFrameHeights[3])),
            Actions.delay(0.3f)
        ));

        if (dieSound != null && controlHUD != null && !controlHUD.isMuted()) {
            System.out.println("Playing Orc_Die sound at volume: " + controlHUD.getEffectVolume());
            dieSound.play(controlHUD.getEffectVolume());
        } else {
            System.out.println("Cannot play Orc_Die sound: " +
                "dieSound=" + (dieSound != null) + ", controlHUD=" + (controlHUD != null) +
                ", isMuted=" + (controlHUD != null ? controlHUD.isMuted() : "N/A"));
        }
    }

    private void updateFacingDirection() {
        if (getSpeed() > 0) {
            float motionAngle = getMotionAngle();
            facingRight = (motionAngle >= 90 && motionAngle <= 270) ? false : true;
            setScale(facingRight ? 1 : -1, 1);
        }
    }

    public boolean isAttacking() {
        return state == State.ATTACKING;
    }

    public boolean isDead() {
        return state == State.DEAD;
    }

    public int getHealth() {
        return health;
    }

    public float getAttackRadius() {
        return attackRadius;
    }

    public void dispose() {
        if (hitSound != null) hitSound.dispose();
        if (dieSound != null) dieSound.dispose();
    }

    private Animation<TextureRegion> getAnimation() {
        try {
            java.lang.reflect.Field field = BaseActor.class.getDeclaredField("animation");
            field.setAccessible(true);
            return (Animation<TextureRegion>) field.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAnimation(Animation<TextureRegion> anim) {
        try {
            java.lang.reflect.Field field = BaseActor.class.getDeclaredField("animation");
            field.setAccessible(true);
            field.set(this, anim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
