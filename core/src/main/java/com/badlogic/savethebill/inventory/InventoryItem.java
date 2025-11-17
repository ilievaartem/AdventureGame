package com.badlogic.savethebill.inventory;

import com.badlogic.gdx.graphics.Texture;

public class InventoryItem {
    public enum ItemType {
        WEAPON,
        RANGED,
        SHIELD,
        FOOD,
        CONSUMABLE
    }

    private String textureName;
    private Texture texture;
    private ItemType type;
    private String name;
    private int value;
    private String description;

    public InventoryItem(String textureName, ItemType type, String name, int value) {
        this.textureName = textureName;
        this.type = type;
        this.name = name;
        this.value = value;
        this.description = generateDescription();
        loadTexture();
    }

    private void loadTexture() {
        try {
            this.texture = new Texture(textureName);
        } catch (Exception e) {
            System.err.println("Error loading texture " + textureName + ": " + e.getMessage());
        }
    }

    private String generateDescription() {
        switch (type) {
            case WEAPON:
                return "Damage: " + value;
            case RANGED:
                return "Ranged Damage: " + value;
            case SHIELD:
                return "Block Chance: " + value + "%";
            case FOOD:
                return "Heals: " + value + " hearts";
            default:
                return "Unknown item";
        }
    }

    public String getTextureName() {
        return textureName;
    }

    public Texture getTexture() {
        return texture;
    }

    public ItemType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public String serialize() {
        return textureName + "|" + type.name() + "|" + name + "|" + value;
    }

    public static InventoryItem deserialize(String data) {
        try {
            String[] parts = data.split("\\|");
            if (parts.length >= 4) {
                String textureName = parts[0];
                ItemType type = ItemType.valueOf(parts[1]);
                String name = parts[2];
                int value = Integer.parseInt(parts[3]);
                return new InventoryItem(textureName, type, name, value);
            }
        } catch (Exception e) {
            System.err.println("Error deserializing item: " + data + " - " + e.getMessage());
        }
        return null;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    @Override
    public String toString() {
        return name + " (" + description + ")";
    }
}
