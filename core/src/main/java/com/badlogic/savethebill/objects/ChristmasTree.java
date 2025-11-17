package com.badlogic.savethebill.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.InventoryManager;

public class ChristmasTree extends BaseActor {
    private InventoryManager inventoryManager;
    private boolean canBeDestroyed = true;
    private Sound destroySound;

    public ChristmasTree(float x, float y, Stage s)
    {
        super(x,y,s);
        loadTexture("christmas-tree.png");
        setBoundaryRectangle();

        try {
            destroySound = Gdx.audio.newSound(Gdx.files.internal("Destroy_Prison.ogg"));
        } catch (Exception e) {
            // Sound not available
        }
    }

    public void setInventoryManager(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    public boolean canBeDestroyedByWeapon() {
        if (inventoryManager != null && canBeDestroyed) {
            return inventoryManager.hasAxeEquipped();
        }
        return false;
    }

    public void destroyIfPossible() {
        if (canBeDestroyedByWeapon()) {
            if (destroySound != null) {
                destroySound.play(0.5f);
            }
            this.remove();
        }
    }

    public boolean canBeDestroyedByCurrentAttack(AttackSystem attackSystem) {
        return canBeDestroyed && attackSystem.canDestroyObjects();
    }

    public boolean isCanBeDestroyed() {
        return canBeDestroyed;
    }
}
