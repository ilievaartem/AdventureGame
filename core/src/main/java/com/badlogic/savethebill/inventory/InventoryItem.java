package com.badlogic.savethebill.inventory;

/**
 * Represents an item in the RPG inventory system
 */
public class InventoryItem {
    private String texture;
    private ItemType type;
    private String name;
    private int damage;
    private int armor;
    private int weight;
    private int value;
    private String description;
    private boolean stackable;
    private int stackSize;
    private int maxStackSize;

    public InventoryItem(String texture, ItemType type, String name, int damage, int weight) {
        this.texture = texture;
        this.type = type;
        this.name = name;
        this.damage = damage;
        this.armor = 0;
        this.weight = weight;
        this.value = damage * 10; // Base value calculation
        this.description = "A " + name.toLowerCase();
        this.stackable = false;
        this.stackSize = 1;
        this.maxStackSize = 1;
    }

    public InventoryItem(String texture, ItemType type, String name, int damage, int armor, int weight, int value, boolean stackable, int maxStackSize) {
        this.texture = texture;
        this.type = type;
        this.name = name;
        this.damage = damage;
        this.armor = armor;
        this.weight = weight;
        this.value = value;
        this.description = "A " + name.toLowerCase();
        this.stackable = stackable;
        this.stackSize = 1;
        this.maxStackSize = maxStackSize;
    }

    // Getters
    public String getTexture() { return texture; }
    public ItemType getType() { return type; }
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getArmor() { return armor; }
    public int getWeight() { return weight; }
    public int getValue() { return value; }
    public String getDescription() { return description; }
    public boolean isStackable() { return stackable; }
    public int getStackSize() { return stackSize; }
    public int getMaxStackSize() { return maxStackSize; }

    // Setters
    public void setStackSize(int stackSize) {
        this.stackSize = Math.min(stackSize, maxStackSize);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Stack management
    public boolean canStackWith(InventoryItem other) {
        return stackable && other.stackable &&
               texture.equals(other.texture) &&
               type == other.type &&
               name.equals(other.name);
    }

    public int addToStack(int amount) {
        int canAdd = Math.min(amount, maxStackSize - stackSize);
        stackSize += canAdd;
        return amount - canAdd; // Return remaining amount that couldn't be added
    }

    public int removeFromStack(int amount) {
        int removed = Math.min(amount, stackSize);
        stackSize -= removed;
        return removed;
    }

    // Tooltip info
    public String getTooltipText() {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(name).append("\n");

        if (damage > 0) {
            tooltip.append("Damage: ").append(damage).append("\n");
        }
        if (armor > 0) {
            tooltip.append("Armor: ").append(armor).append("\n");
        }
        tooltip.append("Weight: ").append(weight).append("\n");
        tooltip.append("Value: ").append(value).append(" coins\n");

        if (stackable && stackSize > 1) {
            tooltip.append("Stack: ").append(stackSize).append("/").append(maxStackSize).append("\n");
        }

        tooltip.append("\n").append(description);

        return tooltip.toString();
    }

    // Serialization for save/load
    public String getDataString() {
        return texture + "," + type.name() + "," + name + "," + damage + "," + armor + "," +
               weight + "," + value + "," + stackable + "," + stackSize + "," + maxStackSize + "," + description;
    }

    public static InventoryItem fromDataString(String data) {
        if (data == null || data.equals("empty")) {
            return null;
        }

        String[] parts = data.split(",");
        if (parts.length >= 10) {
            InventoryItem item = new InventoryItem(
                parts[0], // texture
                ItemType.valueOf(parts[1]), // type
                parts[2], // name
                Integer.parseInt(parts[3]), // damage
                Integer.parseInt(parts[4]), // armor
                Integer.parseInt(parts[5]), // weight
                Integer.parseInt(parts[6]), // value
                Boolean.parseBoolean(parts[7]), // stackable
                Integer.parseInt(parts[9]) // maxStackSize
            );
            item.setStackSize(Integer.parseInt(parts[8])); // stackSize
            if (parts.length > 10) {
                item.setDescription(parts[10]);
            }
            return item;
        }

        return null;
    }

    // Factory methods for creating common items
    public static InventoryItem createSword(int level) {
        String[] swordNames = {"Rusty Sword", "Iron Sword", "Steel Sword", "Enchanted Sword", "Legendary Sword"};
        String[] swordTextures = {"sword-inv.png", "sword-inv.png", "sword-inv.png", "sword-inv.png", "sword-inv.png"};
        int damage = level + 1;
        int weight = 3 + level;
        int value = damage * 15;

        InventoryItem sword = new InventoryItem(
            swordTextures[Math.min(level, 4)],
            ItemType.WEAPON,
            swordNames[Math.min(level, 4)],
            damage,
            0,
            weight,
            value,
            false,
            1
        );
        sword.setDescription("A reliable melee weapon for close combat.");
        return sword;
    }

    public static InventoryItem createBow(int level) {
        String[] bowNames = {"Hunting Bow", "Longbow", "Composite Bow", "Elven Bow", "Dragonbone Bow"};
        String[] bowTextures = {"bow-inv.png", "bow-inv.png", "bow-inv.png", "bow-inv.png", "bow-inv.png"};
        int damage = level + 1;
        int weight = 2 + level;
        int value = damage * 12;

        InventoryItem bow = new InventoryItem(
            bowTextures[Math.min(level, 4)],
            ItemType.RANGED,
            bowNames[Math.min(level, 4)],
            damage,
            0,
            weight,
            value,
            false,
            1
        );
        bow.setDescription("A ranged weapon that requires arrows to use.");
        return bow;
    }

    public static InventoryItem createArmor(int level) {
        String[] armorNames = {"Leather Armor", "Chain Mail", "Plate Armor", "Dragon Scale Armor", "Mythril Armor"};
        String[] armorTextures = {"armor-inv.png", "armor-inv.png", "armor-inv.png", "armor-inv.png", "armor-inv.png"};
        int armor = level + 1;
        int weight = 5 + level * 2;
        int value = armor * 20;

        InventoryItem armorItem = new InventoryItem(
            armorTextures[Math.min(level, 4)],
            ItemType.ARMOR,
            armorNames[Math.min(level, 4)],
            0,
            armor,
            weight,
            value,
            false,
            1
        );
        armorItem.setDescription("Protective chest armor that reduces incoming damage.");
        return armorItem;
    }

    public static InventoryItem createHelmet(int level) {
        String[] helmetNames = {"Leather Cap", "Iron Helmet", "Steel Helm", "Knight's Helmet", "Crown of Kings"};
        String[] helmetTextures = {"helmet-inv.png", "helmet-inv.png", "helmet-inv.png", "helmet-inv.png", "helmet-inv.png"};
        int armor = level;
        int weight = 2 + level;
        int value = armor * 15;

        InventoryItem helmet = new InventoryItem(
            helmetTextures[Math.min(level, 4)],
            ItemType.HELMET,
            helmetNames[Math.min(level, 4)],
            0,
            armor,
            weight,
            value,
            false,
            1
        );
        helmet.setDescription("Head protection that provides additional armor.");
        return helmet;
    }

    public static InventoryItem createShield(int level) {
        String[] shieldNames = {"Wooden Shield", "Iron Shield", "Steel Shield", "Tower Shield", "Aegis Shield"};
        String[] shieldTextures = {"shield-inv.png", "shield-inv.png", "shield-inv.png", "shield-inv.png", "shield-inv.png"};
        int armor = level + 1;
        int weight = 4 + level;
        int value = armor * 18;

        InventoryItem shield = new InventoryItem(
            shieldTextures[Math.min(level, 4)],
            ItemType.SHIELD,
            shieldNames[Math.min(level, 4)],
            0,
            armor,
            weight,
            value,
            false,
            1
        );
        shield.setDescription("A defensive shield that blocks incoming attacks.");
        return shield;
    }

    public static InventoryItem createRing(String type) {
        switch (type.toLowerCase()) {
            case "strength":
                return new InventoryItem("ring-inv.png", ItemType.ACCESSORY, "Ring of Strength", 2, 0, 1, 50, false, 1);
            case "protection":
                return new InventoryItem("ring-inv.png", ItemType.ACCESSORY, "Ring of Protection", 0, 1, 1, 40, false, 1);
            case "speed":
                return new InventoryItem("ring-inv.png", ItemType.ACCESSORY, "Ring of Speed", 0, 0, 1, 30, false, 1);
            default:
                return new InventoryItem("ring-inv.png", ItemType.ACCESSORY, "Simple Ring", 0, 0, 1, 10, false, 1);
        }
    }

    public static InventoryItem createPotion(String type) {
        switch (type.toLowerCase()) {
            case "health":
                InventoryItem healthPotion = new InventoryItem("potion-red.png", ItemType.CONSUMABLE, "Health Potion", 0, 0, 1, 25, true, 10);
                healthPotion.setDescription("Restores health when consumed.");
                return healthPotion;
            case "mana":
                InventoryItem manaPotion = new InventoryItem("potion-blue.png", ItemType.CONSUMABLE, "Mana Potion", 0, 0, 1, 30, true, 10);
                manaPotion.setDescription("Restores magical energy when consumed.");
                return manaPotion;
            case "strength":
                InventoryItem strengthPotion = new InventoryItem("potion-yellow.png", ItemType.CONSUMABLE, "Strength Potion", 0, 0, 1, 50, true, 5);
                strengthPotion.setDescription("Temporarily increases damage output.");
                return strengthPotion;
            default:
                return new InventoryItem("potion-green.png", ItemType.CONSUMABLE, "Unknown Potion", 0, 0, 1, 10, true, 10);
        }
    }

    public static InventoryItem createMaterial(String type) {
        switch (type.toLowerCase()) {
            case "iron_ore":
                InventoryItem ironOre = new InventoryItem("ore-iron.png", ItemType.MATERIAL, "Iron Ore", 0, 0, 2, 5, true, 50);
                ironOre.setDescription("Raw iron ore used for crafting weapons and armor.");
                return ironOre;
            case "gold_ore":
                InventoryItem goldOre = new InventoryItem("ore-gold.png", ItemType.MATERIAL, "Gold Ore", 0, 0, 1, 15, true, 50);
                goldOre.setDescription("Precious gold ore used for crafting valuable items.");
                return goldOre;
            case "leather":
                InventoryItem leather = new InventoryItem("leather.png", ItemType.MATERIAL, "Leather", 0, 0, 1, 8, true, 30);
                leather.setDescription("Tanned animal hide used for crafting armor.");
                return leather;
            case "gem":
                InventoryItem gem = new InventoryItem("gem.png", ItemType.MATERIAL, "Precious Gem", 0, 0, 1, 100, true, 10);
                gem.setDescription("A rare gem used for enhancing equipment.");
                return gem;
            default:
                return new InventoryItem("material.png", ItemType.MATERIAL, "Unknown Material", 0, 0, 1, 1, true, 20);
        }
    }

    public static InventoryItem createQuestItem(String name, String texture) {
        InventoryItem questItem = new InventoryItem(texture, ItemType.QUEST, name, 0, 0, 1, 0, false, 1);
        questItem.setDescription("An important quest item. Do not sell or drop.");
        return questItem;
    }

    // Create random item by level
    public static InventoryItem createRandomItem(int level) {
        int itemType = (int)(Math.random() * 7);

        switch (itemType) {
            case 0: return createSword(level);
            case 1: return createBow(level);
            case 2: return createArmor(level);
            case 3: return createHelmet(level);
            case 4: return createShield(level);
            case 5: return createPotion("health");
            case 6: return createMaterial("iron_ore");
            default: return createSword(1);
        }
    }

    @Override
    public String toString() {
        return name + " (Damage: " + damage + ", Armor: " + armor + ", Weight: " + weight + ")";
    }
}
