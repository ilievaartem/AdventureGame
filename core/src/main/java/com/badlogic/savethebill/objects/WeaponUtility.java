package com.badlogic.savethebill.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class WeaponUtility {

    public static void updateWeaponTexture(Sword sword, InventoryManager inventoryManager) {
        if (sword == null || inventoryManager == null) return;

        sword.setInventoryManager(inventoryManager);
        sword.updateWeaponTexture();
    }

    public static WeaponSwingData getWeaponSwingData(InventoryManager inventoryManager) {
        if (inventoryManager == null) {
            return new WeaponSwingData(90f, 0.25f); // Default sword values
        }

        String weaponType = inventoryManager.getEquippedWeaponType();

        if ("AXE".equals(weaponType)) {
            return new WeaponSwingData(120f, 0.4f); // Axe: larger arc, slower swing
        } else {
            return new WeaponSwingData(90f, 0.25f); // Sword: normal arc and speed
        }
    }

    public static java.util.List<String> handleObjectDestruction(
        InventoryManager inventoryManager,
        Stage mainStage,
        float heroX,
        float heroY,
        float destructionRange) {

        java.util.List<String> destroyedObjectIds = new java.util.ArrayList<>();

        if (inventoryManager == null || !inventoryManager.hasAxeEquipped()) {
            return destroyedObjectIds;
        }

        for (BaseActor actor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Bush")) {
            if (actor instanceof Bush) {
                Bush bush = (Bush) actor;
                bush.setInventoryManager(inventoryManager);
                float distance = Vector2.dst(heroX, heroY, bush.getX(), bush.getY());
                if (distance < destructionRange && bush.canBeDestroyedByWeapon()) {
                    bush.destroyIfPossible();
                    String bushId = "bush_" + (int) bush.getX() + "_" + (int) bush.getY();
                    destroyedObjectIds.add(bushId);
                }
            }
        }

        for (BaseActor actor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            try {
                if (actor instanceof ChristmasTree) {
                    ChristmasTree tree = (ChristmasTree) actor;
                    tree.setInventoryManager(inventoryManager);
                    float distance = Vector2.dst(heroX, heroY, tree.getX(), tree.getY());
                    if (distance < destructionRange && tree.canBeDestroyedByWeapon()) {
                        tree.destroyIfPossible();
                        String treeId = "tree_" + (int) tree.getX() + "_" + (int) tree.getY();
                        destroyedObjectIds.add(treeId);
                    }
                }
            } catch (Exception e) {
                // Safely ignore if ChristmasTree doesn't exist or can't be cast
            }
        }

        return destroyedObjectIds;
    }

    public static int getWeaponDamage(InventoryManager inventoryManager) {
        if (inventoryManager != null) {
            return inventoryManager.getEnhancedWeaponDamage();
        }
        return 1;
    }

    public static int getRangedDamage(InventoryManager inventoryManager) {
        if (inventoryManager != null) {
            return inventoryManager.calculateDamageToEnemy(true); // true for ranged attack
        }
        return 1;
    }

    public static class WeaponSwingData {
        public final float weaponArc;
        public final float swingDuration;

        public WeaponSwingData(float weaponArc, float swingDuration) {
            this.weaponArc = weaponArc;
            this.swingDuration = swingDuration;
        }
    }
}
