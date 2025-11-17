package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class Sword extends BaseActor {
    private InventoryManager inventoryManager;
    private String currentWeaponTexture;
    private boolean textureNeedsUpdate;

    public Sword(float x, float y, Stage s) {
        super(x, y, s);
        currentWeaponTexture = "sword.png";
        loadTexture(currentWeaponTexture);
        textureNeedsUpdate = false;
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        updateWeaponTexture();
    }

    public void updateWeaponTexture() {
        if (inventoryManager == null) return;

        String newTexture = inventoryManager.getAttackTextureForWeapon();

        if (!newTexture.equals(currentWeaponTexture)) {
            currentWeaponTexture = newTexture;
            loadTexture(currentWeaponTexture);
            textureNeedsUpdate = false;
        }
    }

    @Override
    public void act(float dt) {
        super.act(dt);

        updateWeaponTexture();
    }

    public int getDamage() {
        if (inventoryManager != null) {
            return inventoryManager.getEnhancedWeaponDamage();
        }
        return 1;
    }

    public boolean isAxeEquipped() {
        if (inventoryManager != null) {
            return inventoryManager.hasAxeEquipped();
        }
        return false;
    }

    public String getWeaponType() {
        if (inventoryManager != null) {
            return inventoryManager.getEquippedWeaponType();
        }
        return "NONE";
    }

    public String getCurrentWeaponTexture() {
        return currentWeaponTexture;
    }
}
