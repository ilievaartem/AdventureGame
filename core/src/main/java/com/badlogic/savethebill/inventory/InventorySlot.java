package com.badlogic.savethebill.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class InventorySlot {
    public enum SlotType {
        GENERAL,
        WEAPON,
        RANGED,
        SHIELD
    }

    private SlotType type;
    private InventoryItem item;
    private Image slotImage;
    private Image itemImage;
    private float x, y;
    private boolean visible;
    private Stage stage;

    private static final float SLOT_SIZE = 48f;

    public InventorySlot(float x, float y, SlotType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.visible = false;

        try {
            slotImage = new Image(new Texture("slot.png"));
            slotImage.setSize(SLOT_SIZE, SLOT_SIZE);
            slotImage.setPosition(x, y);
            slotImage.setVisible(false);
        } catch (Exception e) {
            System.err.println("Error loading slot.png: " + e.getMessage());
            slotImage = new Image();
            slotImage.setSize(SLOT_SIZE, SLOT_SIZE);
            slotImage.setPosition(x, y);
            slotImage.setVisible(false);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null && slotImage != null) {
            stage.addActor(slotImage);
        }
    }

    public boolean canAcceptItem(InventoryItem item) {
        if (item == null) return true;

        switch (type) {
            case GENERAL:
                return true;
            case WEAPON:
                return item.getType() == InventoryItem.ItemType.WEAPON;
            case RANGED:
                return item.getType() == InventoryItem.ItemType.RANGED;
            case SHIELD:
                return item.getType() == InventoryItem.ItemType.SHIELD;
            default:
                return false;
        }
    }

    public void setItem(InventoryItem newItem) {
        if (itemImage != null && stage != null) {
            itemImage.remove();
            itemImage = null;
        }

        this.item = newItem;

        if (newItem != null) {
            try {
                itemImage = new Image(newItem.getTexture());
                itemImage.setSize(SLOT_SIZE - 4, SLOT_SIZE - 4);
                itemImage.setPosition(x + 2, y + 2);
                itemImage.setVisible(visible);

                if (stage != null) {
                    stage.addActor(itemImage);
                }
            } catch (Exception e) {
                System.err.println("Error creating item image: " + e.getMessage());
            }
        }
    }

    public InventoryItem getItem() {
        return item;
    }

    public Image getImage() {
        return slotImage;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (slotImage != null) {
            slotImage.setVisible(visible);
        }
        if (itemImage != null) {
            itemImage.setVisible(visible);
        }
    }

    public void updatePosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;

        if (slotImage != null) {
            slotImage.setPosition(x, y);
        }
        if (itemImage != null) {
            itemImage.setPosition(x + 2, y + 2);
        }
    }

    public boolean containsPoint(float pointX, float pointY) {
        return pointX >= x && pointX <= x + SLOT_SIZE &&
            pointY >= y && pointY <= y + SLOT_SIZE;
    }

    public SlotType getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void dispose() {
        if (itemImage != null) {
            itemImage.remove();
        }
        if (slotImage != null) {
            slotImage.remove();
        }
    }
}
