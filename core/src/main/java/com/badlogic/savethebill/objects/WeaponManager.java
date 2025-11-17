package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class WeaponManager extends BaseActor {
    private InventoryManager inventoryManager;
    private AttackSystem attackSystem;
    private Stage gameStage;

    private static final float SWORD_BASIC_DAMAGE = 3.0f;
    private static final float SWORD_SECOND_DAMAGE = 5.0f;
    private static final float SWORD_THIRD_DAMAGE = 7.0f;
    private static final float AXE_DAMAGE = 4.0f;

    public WeaponManager(float x, float y, Stage gameStage) {
        super(x, y, gameStage);
        this.gameStage = gameStage;
        this.attackSystem = new AttackSystem(x, y, gameStage);
        setVisible(false);
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        if (attackSystem != null) {
            attackSystem.setInventoryManager(inventoryManager);
        }
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        if (attackSystem != null) {
            attackSystem.act(dt);

            attackSystem.setAttackPosition(getX(), getY());

            if (attackSystem.isAttacking()) {
                handleObjectDestruction();
            }
        }
    }

    private void handleObjectDestruction() {
        if (inventoryManager == null || !inventoryManager.hasAxeEquipped()) {
            return;
        }

        for (BaseActor actor : BaseActor.getList(gameStage, "com.badlogic.savethebill.objects.Bush")) {
            if (actor instanceof Bush) {
                Bush bush = (Bush) actor;
                if (bush.overlaps(attackSystem) && bush.canBeDestroyedByWeapon()) {
                    bush.destroyIfPossible();
                }
            }
        }

        for (BaseActor actor : BaseActor.getList(gameStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            if (actor instanceof ChristmasTree) {
                ChristmasTree tree = (ChristmasTree) actor;
                if (tree.overlaps(attackSystem) && tree.canBeDestroyedByWeapon()) {
                    tree.destroyIfPossible();
                }
            }
        }
    }

    public int getCurrentWeaponDamage() {
        if (inventoryManager == null) {
            return 1;
        }

        String weaponType = inventoryManager.getEquippedWeaponType();
        String weaponTexture = inventoryManager.getEquippedWeaponTexture();

        if ("AXE".equals(weaponType)) {
            return (int) AXE_DAMAGE;
        } else if ("SWORD".equals(weaponType)) {
            if ("sword.png".equals(weaponTexture)) {
                return (int) SWORD_BASIC_DAMAGE;
            } else if ("sword-second-level.png".equals(weaponTexture)) {
                return (int) SWORD_SECOND_DAMAGE;
            } else if ("sword-third-level.png".equals(weaponTexture)) {
                return (int) SWORD_THIRD_DAMAGE;
            }
        }

        return 1;
    }

    public String getCurrentAttackTexture() {
        if (inventoryManager == null) {
            return "sword.png";
        }

        String weaponType = inventoryManager.getEquippedWeaponType();

        if ("AXE".equals(weaponType)) {
            return "axe.png";
        } else {
            return inventoryManager.getEquippedWeaponTexture();
        }
    }

    public boolean canDestroyObjects() {
        return inventoryManager != null && inventoryManager.hasAxeEquipped();
    }

    public boolean isAttacking() {
        return attackSystem != null && attackSystem.isAttacking();
    }

    public AttackSystem getAttackSystem() {
        return attackSystem;
    }

    public void updatePosition(float x, float y) {
        setPosition(x, y);
    }
}
