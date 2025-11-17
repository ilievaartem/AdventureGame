package com.badlogic.savethebill;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.savethebill.inventory.InventoryItem;
import com.badlogic.savethebill.inventory.RPGInventorySystem;

public class InventoryManager {
    private RPGInventorySystem inventorySystem;
    private Stage uiStage;

    public InventoryManager(Stage uiStage) {
        this.uiStage = uiStage;
        this.inventorySystem = new RPGInventorySystem(uiStage);

        inventorySystem.setFoodConsumeListener(new RPGInventorySystem.FoodConsumeListener() {
            @Override
            public void onFoodConsumed(int healAmount) {
                if (foodConsumeListener != null) {
                    foodConsumeListener.onFoodConsumed(healAmount);
                }
            }
        });

        addStartingItems();
    }

    private void addStartingItems() {
    }

    public void update() {
        if (inventorySystem != null) {
            inventorySystem.update();
        }
    }

    public boolean canAttackWithMelee() {
        return inventorySystem != null && inventorySystem.canAttackWithWeapon();
    }

    public boolean canAttackWithRanged() {
        return inventorySystem != null && inventorySystem.canAttackWithRanged();
    }

    public int getMeleeDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentWeaponDamage() : 1;
    }

    public int getRangedDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentRangedDamage() : 1;
    }

    public int getArmor() {
        return inventorySystem != null ? inventorySystem.getCurrentArmor() : 0;
    }

    public boolean isGameFrozen() {
        return inventorySystem != null && inventorySystem.isGameFrozen();
    }

    public boolean isInventoryVisible() {
        return inventorySystem != null && inventorySystem.isInventoryVisible();
    }

    public String getEquippedWeaponType() {
        return inventorySystem != null ? inventorySystem.getEquippedWeaponType() : "NONE";
    }

    public String getEquippedWeaponTexture() {
        return inventorySystem != null ? inventorySystem.getEquippedWeaponTexture() : "sword.png";
    }

    public String getEquippedBowTexture() {
        return inventorySystem != null ? inventorySystem.getEquippedBowTexture() : "bow.png";
    }

    public boolean hasAxeEquipped() {
        return inventorySystem != null && inventorySystem.hasAxeEquipped();
    }

    public boolean canBlockAttack() {
        return inventorySystem != null && inventorySystem.canBlockAttack();
    }

    public boolean attemptBlock() {
        return inventorySystem != null && inventorySystem.attemptBlock();
    }

    public int getShieldBlockChance() {
        return inventorySystem != null ? inventorySystem.getShieldBlockChance() : 0;
    }

    public int calculateDamageToEnemy(boolean isRangedAttack) {
        if (inventorySystem == null) return 1;

        if (isRangedAttack) {
            int bowDamage = inventorySystem.getCurrentRangedDamage();
            return Math.max(1, (int) (bowDamage * 0.8f));
        } else {
            return getEnhancedWeaponDamage();
        }
    }

    public int getEnhancedWeaponDamage() {
        if (inventorySystem == null) return 1;

        String weaponType = getEquippedWeaponType();
        String weaponTexture = getEquippedWeaponTexture();

        if ("AXE".equals(weaponType)) {
            return 4;
        } else if ("SWORD".equals(weaponType)) {
            if ("sword.png".equals(weaponTexture)) {
                return 3;
            } else if ("sword-second-level.png".equals(weaponTexture)) {
                return 5;
            } else if ("sword-third-level.png".equals(weaponTexture)) {
                return 7;
            }
        }

        return inventorySystem.getCurrentWeaponDamage();
    }

    public String getAttackTextureForWeapon() {
        if (inventorySystem == null) return "sword.png";

        String weaponType = getEquippedWeaponType();
        String equippedTexture = getEquippedWeaponTexture();

        if ("AXE".equals(weaponType)) {
            return "axe.png";
        } else if ("SWORD".equals(weaponType)) {
            return equippedTexture;
        }

        return equippedTexture;
    }

    public int calculateDamageToPlayer(int incomingDamage) {
        if (inventorySystem == null) return incomingDamage;

        int finalDamage = incomingDamage;

        if (canBlockAttack() && attemptBlock()) {
            return 0;
        }

        int armorReduction = getArmor();
        finalDamage = Math.max(1, finalDamage - armorReduction);

        return finalDamage;
    }

    public int getCurrentWeaponDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentWeaponDamage() : 1;
    }

    public int getCurrentRangedDamage() {
        return inventorySystem != null ? inventorySystem.getCurrentRangedDamage() : 1;
    }

    public int getCurrentArmor() {
        return inventorySystem != null ? inventorySystem.getCurrentArmor() : 0;
    }

    public boolean addItem(InventoryItem item) {
        return inventorySystem != null && inventorySystem.addItem(item);
    }

    public void addItemByType(String itemType, int level) {
        InventoryItem item = createItemByType(itemType, level);
        if (item != null) {
            addItem(item);
        }
    }

    private InventoryItem createItemByType(String itemType, int level) {
        switch (itemType.toLowerCase()) {
            case "sword":
                switch (level) {
                    case 0:
                        return new InventoryItem("sword-inv.png", InventoryItem.ItemType.WEAPON, "Basic Sword", 2);
                    case 1:
                        return new InventoryItem("sword-inv-second-level.png", InventoryItem.ItemType.WEAPON, "Steel Sword", 4);
                    case 2:
                        return new InventoryItem("sword-inv-third-level.png", InventoryItem.ItemType.WEAPON, "Master Sword", 6);
                }
                break;
            case "bow":
                switch (level) {
                    case 0:
                        return new InventoryItem("bow-inv.png", InventoryItem.ItemType.RANGED, "Basic Bow", 2);
                    case 1:
                        return new InventoryItem("bow-inv-second-level.png", InventoryItem.ItemType.RANGED, "Hunter Bow", 3);
                    case 2:
                        return new InventoryItem("bow-inv-third-level.png", InventoryItem.ItemType.RANGED, "Master Bow", 4);
                }
                break;
            case "shield":
                switch (level) {
                    case 0:
                        return new InventoryItem("shield-inv.png", InventoryItem.ItemType.SHIELD, "Basic Shield", 20);
                    case 1:
                        return new InventoryItem("shield-inv-second-level.png", InventoryItem.ItemType.SHIELD, "Steel Shield", 35);
                    case 2:
                        return new InventoryItem("shield-inv-third-level.png", InventoryItem.ItemType.SHIELD, "Master Shield", 50);
                }
                break;
            case "axe":
                return new InventoryItem("axe-inv.png", InventoryItem.ItemType.WEAPON, "Axe", 3);
            case "health_potion":
                return new InventoryItem("food-inv.png", InventoryItem.ItemType.FOOD, "Health Potion", 2);
        }
        return null;
    }

    public String saveInventoryToGame() {
        return "";
    }

    public void loadInventoryFromGame(String data) {
    }

    public interface FoodConsumeListener {
        void onFoodConsumed(int healAmount);
    }

    private FoodConsumeListener foodConsumeListener;

    public void setFoodConsumeListener(FoodConsumeListener listener) {
        this.foodConsumeListener = listener;
    }

    public RPGInventorySystem getInventorySystem() {
        return inventorySystem;
    }

    public void dispose() {
        if (inventorySystem != null) {
            inventorySystem.dispose();
        }
    }
}
