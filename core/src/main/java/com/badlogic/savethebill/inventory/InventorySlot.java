package com.badlogic.savethebill.inventory;

import com.badlogic.savethebill.BaseActor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Enhanced inventory slot that can hold an item with visual feedback
 */
public class InventorySlot {
    private InventoryItem item;
    private ItemType acceptedType;
    private BaseActor slotBackground;
    private BaseActor itemActor;
    private BaseActor highlightActor;
    private Label stackLabel;
    private boolean visible = false;
    private boolean highlighted = false;

    public InventorySlot(ItemType acceptedType) {
        this.acceptedType = acceptedType;
    }

    public void setBounds(float x, float y, float width, float height) {
        if (slotBackground == null) {
            // Create background actor for the slot
            slotBackground = new BaseActor(x, y, null);
            slotBackground.setSize(width, height);
            slotBackground.setUserObject(this); // Reference back to this slot

            // Create highlight overlay
            highlightActor = new BaseActor(x, y, null);
            highlightActor.setSize(width, height);
            highlightActor.setVisible(false);
        } else {
            slotBackground.setPosition(x, y);
            slotBackground.setSize(width, height);

            if (highlightActor != null) {
                highlightActor.setPosition(x, y);
                highlightActor.setSize(width, height);
            }
        }

        updateVisual();
    }

    public void setBackgroundTexture(String texture) {
        if (slotBackground != null) {
            slotBackground.loadTexture(texture);
        }
    }

    public BaseActor getSlotActor() {
        return slotBackground;
    }

    public boolean canAcceptItem(InventoryItem item) {
        if (item == null) return true;

        // Check if this slot can accept the item type
        if (acceptedType != ItemType.ANY && acceptedType != item.getType()) {
            return false;
        }

        // If slot has an item, check if they can stack
        if (this.item != null) {
            return this.item.canStackWith(item);
        }

        return true;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
        updateVisual();
    }

    public InventoryItem getItem() {
        return item;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (slotBackground != null) {
            slotBackground.setVisible(visible);
        }
        if (itemActor != null) {
            itemActor.setVisible(visible);
        }
        if (highlightActor != null && highlighted) {
            highlightActor.setVisible(visible);
        }
        if (stackLabel != null) {
            stackLabel.setVisible(visible && item != null && item.isStackable() && item.getStackSize() > 1);
        }
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        if (highlightActor != null) {
            highlightActor.setVisible(highlighted && visible);
            if (highlighted) {
                highlightActor.setColor(Color.YELLOW);
                highlightActor.setOpacity(0.3f);
            }
        }
    }

    private void updateVisual() {
        // Remove old item actor if exists
        if (itemActor != null) {
            itemActor.remove();
            itemActor = null;
        }

        // Remove old stack label if exists
        if (stackLabel != null) {
            stackLabel.remove();
            stackLabel = null;
        }

        // Create new item actor if item exists
        if (item != null && slotBackground != null) {
            Stage stage = slotBackground.getStage();
            if (stage != null) {
                itemActor = new BaseActor(0, 0, stage);
                itemActor.loadTexture(item.getTexture());

                // Size item to fit nicely in slot (slightly smaller than slot)
                float itemSize = Math.min(slotBackground.getWidth(), slotBackground.getHeight()) * 0.8f;
                itemActor.setSize(itemSize, itemSize);

                // Center item in slot
                float itemX = slotBackground.getX() + (slotBackground.getWidth() - itemSize) / 2f;
                float itemY = slotBackground.getY() + (slotBackground.getHeight() - itemSize) / 2f;
                itemActor.setPosition(itemX, itemY);

                itemActor.setVisible(visible);

                // Add stack size label for stackable items
                if (item.isStackable() && item.getStackSize() > 1) {
                    stackLabel = new Label(String.valueOf(item.getStackSize()),
                        com.badlogic.savethebill.BaseGame.labelStyle);
                    stackLabel.setFontScale(0.4f);
                    stackLabel.setColor(Color.WHITE);
                    stackLabel.setPosition(
                        slotBackground.getX() + slotBackground.getWidth() - 12,
                        slotBackground.getY() + 2
                    );
                    stackLabel.setVisible(visible);
                    stage.addActor(stackLabel);
                }
            }
        }
    }

    public boolean isEmpty() {
        return item == null;
    }

    public void clear() {
        setItem(null);
    }

    // For tooltip display
    public String getTooltipText() {
        return item != null ? item.getTooltipText() : "";
    }

    public ItemType getAcceptedType() {
        return acceptedType;
    }

    // Stack management
    public boolean addToStack(InventoryItem newItem) {
        if (item != null && item.canStackWith(newItem)) {
            int remaining = item.addToStack(newItem.getStackSize());
            updateVisual();
            return remaining == 0;
        }
        return false;
    }

    public InventoryItem removeFromStack(int amount) {
        if (item != null && item.isStackable()) {
            int removed = item.removeFromStack(amount);
            if (item.getStackSize() <= 0) {
                InventoryItem removedItem = item;
                setItem(null);
                return removedItem;
            } else {
                updateVisual();
                // Create a new item with the removed amount
                InventoryItem newItem = new InventoryItem(
                    item.getTexture(),
                    item.getType(),
                    item.getName(),
                    item.getDamage(),
                    item.getArmor(),
                    item.getWeight(),
                    item.getValue(),
                    item.isStackable(),
                    item.getMaxStackSize()
                );
                newItem.setStackSize(removed);
                return newItem;
            }
        }
        return null;
    }

    // Quality of life methods
    public boolean hasItem() {
        return item != null;
    }

    public boolean isEquipmentSlot() {
        return acceptedType != ItemType.ANY;
    }

    public void dispose() {
        if (itemActor != null) {
            itemActor.remove();
        }
        if (slotBackground != null) {
            slotBackground.remove();
        }
        if (highlightActor != null) {
            highlightActor.remove();
        }
        if (stackLabel != null) {
            stackLabel.remove();
        }
    }
}
