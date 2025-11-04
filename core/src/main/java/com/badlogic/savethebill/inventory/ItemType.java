package com.badlogic.savethebill.inventory;

/**
 * Enum for different types of items in RPG inventory
 */
public enum ItemType {
    WEAPON,      // Swords, axes, maces (main hand weapons)
    RANGED,      // Bows, crossbows (ranged weapons)
    ARMOR,       // Chest armor, body armor
    HELMET,      // Head protection
    SHIELD,      // Off-hand shields
    ACCESSORY,   // Rings, amulets, jewelry
    CONSUMABLE,  // Potions, food, scrolls
    MATERIAL,    // Crafting materials, gems
    QUEST,       // Quest items, key items
    ANY          // Can accept any item type (for general inventory slots)
}
