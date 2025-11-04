package com.badlogic.savethebill.inventory;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.SaveManager;

/**
 * Global inventory manager that persists across all game screens
 */
public class InventoryManager {
    private static InventoryManager instance;
    private RPGInventorySystem inventorySystem;
    private boolean initialized = false;

    private InventoryManager() {
        // Private constructor for singleton
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    public void initializeForScreen(Stage uiStage) {
        if (inventorySystem != null) {
            // Remove from previous stage
            inventorySystem.remove();
        }

        // Create new inventory system for this stage
        inventorySystem = new RPGInventorySystem(uiStage);

        // Load saved inventory data if available
        if (!initialized) {
            loadInventoryFromSave();
            // Add starting items only if no save data exists
            if (getInventoryDataString().isEmpty()) {
                inventorySystem.addStartingItems();
            }
            initialized = true;
        }
    }

    public void update() {
        if (inventorySystem != null) {
            inventorySystem.update();
        }
    }

    public boolean isGameFrozen() {
        return inventorySystem != null && inventorySystem.isGameFrozen();
    }

    public boolean isInventoryVisible() {
        return inventorySystem != null && inventorySystem.isInventoryVisible();
    }

    // Combat methods
    public boolean canAttackWithWeapon() {
        return inventorySystem != null && inventorySystem.canAttackWithWeapon();
    }

    public boolean canAttackWithRanged() {
        return inventorySystem != null && inventorySystem.canAttackWithRanged();
    }

    public int getCurrentWeaponDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentWeaponDamage() : 0;
    }

    public int getCurrentRangedDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentRangedDamage() : 0;
    }

    public int getCurrentArmor() {
        return inventorySystem != null ? inventorySystem.getCurrentArmor() : 0;
    }

    // Item management
    public boolean addItem(InventoryItem item) {
        return inventorySystem != null && inventorySystem.addItem(item);
    }

    public boolean addItemByType(String type, int level) {
        InventoryItem item = null;
        switch (type.toLowerCase()) {
            case "sword":
                item = InventoryItem.createSword(level);
                break;
            case "bow":
                item = InventoryItem.createBow(level);
                break;
            case "armor":
                item = InventoryItem.createArmor(level);
                break;
            case "health_potion":
                item = InventoryItem.createPotion("health");
                break;
            case "mana_potion":
                item = InventoryItem.createPotion("mana");
                break;
        }

        return item != null && addItem(item);
    }

    // Save/Load functionality
    public void saveInventoryToGame() {
        if (inventorySystem != null) {
            String inventoryData = inventorySystem.getInventoryDataString();
            // Save to preferences or game save system
            SaveManager.getInstance().saveInventoryData(inventoryData);
        }
    }

    public void loadInventoryFromSave() {
        if (inventorySystem != null) {
            String inventoryData = SaveManager.getInstance().loadInventoryData();
            if (inventoryData != null && !inventoryData.isEmpty()) {
                inventorySystem.loadInventoryData(inventoryData);
            }
        }
    }

    public String getInventoryDataString() {
        return inventorySystem != null ? inventorySystem.getInventoryDataString() : "";
    }

    public void loadInventoryData(String data) {
        if (inventorySystem != null) {
            inventorySystem.loadInventoryData(data);
        }
    }

    // Cleanup
    public void dispose() {
        if (inventorySystem != null) {
            inventorySystem.remove();
            inventorySystem = null;
        }
        initialized = false;
    }
}
