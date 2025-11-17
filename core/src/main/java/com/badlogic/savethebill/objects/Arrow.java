package com.badlogic.savethebill.objects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class Arrow extends BaseActor {
    private InventoryManager inventoryManager;
    private int baseDamage = 0;

    public Arrow(float x, float y, Stage s) {
        super(x, y, s);
        loadTexture("arrow.png");
        setSpeed(400);
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public int getDamage() {
        if (inventoryManager != null) {
            return inventoryManager.getRangedDamage();
        }
        return baseDamage;
    }

    public void updateArrowTexture() {
        if (inventoryManager != null) {
            String bowTexture = inventoryManager.getEquippedBowTexture();
            loadTexture("arrow.png");
        }
    }

    public void act(float dt) {
        super.act(dt);
        applyPhysics(dt);
    }
}
