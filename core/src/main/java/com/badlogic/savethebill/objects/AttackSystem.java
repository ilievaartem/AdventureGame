package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class AttackSystem extends BaseActor {
    private InventoryManager inventoryManager;
    private boolean isAttacking = false;
    private float attackDuration = 0.3f;
    private float attackTimer = 0f;

    public AttackSystem(float x, float y, Stage s) {
        super(x, y, s);
        setVisible(false);
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (isAttacking) {
            attackTimer += dt;
            if (attackTimer >= attackDuration) {
                endAttack();
            }
        }
    }

    public void startAttack() {
        if (inventoryManager == null) return;

        isAttacking = true;
        attackTimer = 0f;

        if (canDestroyObjects()) {
            handleObjectDestruction();
        }
    }

    private void handleObjectDestruction() {
        Stage stage = getStage();
        if (stage == null) return;

        for (BaseActor actor : BaseActor.getList(stage, "com.badlogic.savethebill.objects.Bush")) {
            if (overlaps(actor)) {
                Bush bush = (Bush) actor;
                if (bush.canBeDestroyedByCurrentAttack(this)) {
                    bush.destroyIfPossible();
                }
            }
        }

        for (BaseActor actor : BaseActor.getList(stage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            if (overlaps(actor)) {
                ChristmasTree tree = (ChristmasTree) actor;
                if (tree.canBeDestroyedByCurrentAttack(this)) {
                    tree.destroyIfPossible();
                }
            }
        }
    }

    public void endAttack() {
        isAttacking = false;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public int getAttackDamage() {
        if (inventoryManager != null) {
            return inventoryManager.getEnhancedWeaponDamage();
        }
        return 1;
    }

    public String getWeaponType() {
        if (inventoryManager != null) {
            return inventoryManager.getEquippedWeaponType();
        }
        return "NONE";
    }

    public boolean canDestroyObjects() {
        return "AXE".equals(getWeaponType());
    }

    public void setAttackPosition(float playerX, float playerY) {
        setPosition(playerX, playerY);
        setSize(64, 64);
    }
}
