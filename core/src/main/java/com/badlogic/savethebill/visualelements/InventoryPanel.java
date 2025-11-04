package com.badlogic.savethebill.visualelements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;

public class InventoryPanel extends Actor {
    private boolean isVisible = false;
    private boolean qKeyPressed = false;
    private Stage uiStage;
    private boolean gameFrozen = false;

    // Background panels
    private BaseActor mainPanel;

    // Equipment slots (4 main slots - separated from main inventory)
    private BaseActor leftHandSlot;    // Sword slot
    private BaseActor rightHandSlot;   // Bow slot
    private BaseActor armorSlot;       // Future armor
    private BaseActor shieldSlot;      // Future shield

    // Equipment items
    private BaseActor swordIcon;
    private BaseActor bowIcon;

    // Damage indicators inside inventory
    private BaseActor swordDamageIcon;
    private BaseActor bowDamageIcon;
    private Label swordDamageLabel;
    private Label bowDamageLabel;

    // General inventory slots (3x3 grid = 9 slots)
    private BaseActor[] inventorySlots;
    private static final int INVENTORY_ROWS = 3;
    private static final int INVENTORY_COLS = 3;
    private static final int TOTAL_INVENTORY_SLOTS = INVENTORY_ROWS * INVENTORY_COLS;

    // Corrected smaller dimensions
    private static final float PANEL_WIDTH = 200;
    private static final float PANEL_HEIGHT = 240;
    private static final float SLOT_SIZE = 32;
    private static final float SLOT_SPACING = 38;

    // Equipment slots positions (top area, properly spaced)
    private static final float EQUIPMENT_START_X = 20;
    private static final float EQUIPMENT_START_Y = 180;
    private static final float EQUIPMENT_SPACING = 80; // Space between left and right equipment

    // General inventory positions (bottom area)
    private static final float INVENTORY_START_X = 20;
    private static final float INVENTORY_START_Y = 30;

    // Damage indicators positions (middle area, centered)
    private static final float DAMAGE_START_X = 50;
    private static final float DAMAGE_START_Y = 130;

    // Default damage values
    private int swordDamage = 1;
    private int bowDamage = 1;

    public InventoryPanel(Stage uiStage) {
        this.uiStage = uiStage;
        initializeInventory();
        uiStage.addActor(this);
    }

    private void initializeInventory() {
        // Create main inventory panel background with proper centering
        mainPanel = new BaseActor(0, 0, uiStage);
        mainPanel.loadTexture("panel.png");
        mainPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);

        // Center the panel properly on screen
        float centerX = (Gdx.graphics.getWidth() - PANEL_WIDTH) / 2f;
        float centerY = (Gdx.graphics.getHeight() - PANEL_HEIGHT) / 2f;
        mainPanel.setPosition(centerX, centerY);
        mainPanel.setVisible(false);

        // Initialize equipment slots (top area, separated)
        initializeEquipmentSlots();

        // Initialize damage indicators (middle area)
        initializeDamageIndicators();

        // Initialize general inventory slots (bottom area)
        initializeGeneralInventory();

        // Initialize equipment items
        initializeEquipmentItems();
    }

    private void initializeEquipmentSlots() {
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        // Left hand slot (sword) - top left
        leftHandSlot = new BaseActor(0, 0, uiStage);
        leftHandSlot.loadTexture("slot.png");
        leftHandSlot.setSize(SLOT_SIZE, SLOT_SIZE);
        leftHandSlot.setPosition(
            panelX + EQUIPMENT_START_X,
            panelY + EQUIPMENT_START_Y
        );
        leftHandSlot.setVisible(false);

        // Right hand slot (bow) - top right
        rightHandSlot = new BaseActor(0, 0, uiStage);
        rightHandSlot.loadTexture("slot.png");
        rightHandSlot.setSize(SLOT_SIZE, SLOT_SIZE);
        rightHandSlot.setPosition(
            panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING,
            panelY + EQUIPMENT_START_Y
        );
        rightHandSlot.setVisible(false);

        // Armor slot (future) - bottom left
        armorSlot = new BaseActor(0, 0, uiStage);
        armorSlot.loadTexture("slot.png");
        armorSlot.setSize(SLOT_SIZE, SLOT_SIZE);
        armorSlot.setPosition(
            panelX + EQUIPMENT_START_X,
            panelY + EQUIPMENT_START_Y - 40
        );
        armorSlot.setVisible(false);

        // Shield slot (future) - bottom right
        shieldSlot = new BaseActor(0, 0, uiStage);
        shieldSlot.loadTexture("slot.png");
        shieldSlot.setSize(SLOT_SIZE, SLOT_SIZE);
        shieldSlot.setPosition(
            panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING,
            panelY + EQUIPMENT_START_Y - 40
        );
        shieldSlot.setVisible(false);
    }

    private void initializeDamageIndicators() {
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        // Sword damage icon and label (left side)
        swordDamageIcon = new BaseActor(0, 0, uiStage);
        swordDamageIcon.loadTexture("damage-sword.png");
        swordDamageIcon.setSize(16, 16);
        swordDamageIcon.setPosition(
            panelX + DAMAGE_START_X - 20,
            panelY + DAMAGE_START_Y
        );
        swordDamageIcon.setVisible(false);

        swordDamageLabel = new Label(String.valueOf(swordDamage), BaseGame.labelStyle);
        swordDamageLabel.setColor(Color.SCARLET);
        swordDamageLabel.setFontScale(0.6f);
        swordDamageLabel.setPosition(
            panelX + DAMAGE_START_X,
            panelY + DAMAGE_START_Y
        );
        swordDamageLabel.setVisible(false);
        uiStage.addActor(swordDamageLabel);

        // Bow damage icon and label (right side)
        bowDamageIcon = new BaseActor(0, 0, uiStage);
        bowDamageIcon.loadTexture("damage-arrow.png");
        bowDamageIcon.setSize(16, 16);
        bowDamageIcon.setPosition(
            panelX + DAMAGE_START_X + 40,
            panelY + DAMAGE_START_Y
        );
        bowDamageIcon.setVisible(false);

        bowDamageLabel = new Label(String.valueOf(bowDamage), BaseGame.labelStyle);
        bowDamageLabel.setColor(Color.ORANGE);
        bowDamageLabel.setFontScale(0.6f);
        bowDamageLabel.setPosition(
            panelX + DAMAGE_START_X + 60,
            panelY + DAMAGE_START_Y
        );
        bowDamageLabel.setVisible(false);
        uiStage.addActor(bowDamageLabel);
    }

    private void initializeGeneralInventory() {
        inventorySlots = new BaseActor[TOTAL_INVENTORY_SLOTS];
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                int index = row * INVENTORY_COLS + col;
                inventorySlots[index] = new BaseActor(0, 0, uiStage);
                inventorySlots[index].loadTexture("slot.png");
                inventorySlots[index].setSize(SLOT_SIZE, SLOT_SIZE);
                inventorySlots[index].setPosition(
                    panelX + INVENTORY_START_X + col * SLOT_SPACING,
                    panelY + INVENTORY_START_Y + (INVENTORY_ROWS - 1 - row) * SLOT_SPACING
                );
                inventorySlots[index].setVisible(false);
            }
        }
    }

    private void initializeEquipmentItems() {
        // Sword icon in left hand slot
        swordIcon = new BaseActor(0, 0, uiStage);
        swordIcon.loadTexture("sword-inv.png");
        swordIcon.setSize(28, 28);
        swordIcon.setPosition(
            leftHandSlot.getX() + (SLOT_SIZE - 28) / 2,
            leftHandSlot.getY() + (SLOT_SIZE - 28) / 2
        );
        swordIcon.setVisible(false);

        // Bow icon in right hand slot
        bowIcon = new BaseActor(0, 0, uiStage);
        bowIcon.loadTexture("bow-inv.png");
        bowIcon.setSize(28, 28);
        bowIcon.setPosition(
            rightHandSlot.getX() + (SLOT_SIZE - 28) / 2,
            rightHandSlot.getY() + (SLOT_SIZE - 28) / 2
        );
        bowIcon.setVisible(false);
    }

    public void update() {
        handleInput();
    }

    private void handleInput() {
        boolean qKeyCurrentlyPressed = Gdx.input.isKeyPressed(Keys.Q);

        // Toggle inventory when Q is pressed (not held)
        if (qKeyCurrentlyPressed && !qKeyPressed) {
            toggleInventory();
        }

        qKeyPressed = qKeyCurrentlyPressed;
    }

    private void toggleInventory() {
        isVisible = !isVisible;
        gameFrozen = isVisible; // Freeze game when inventory is open
        setInventoryVisible(isVisible);
    }

    private void setInventoryVisible(boolean visible) {
        mainPanel.setVisible(visible);

        // Equipment slots
        leftHandSlot.setVisible(visible);
        rightHandSlot.setVisible(visible);
        armorSlot.setVisible(visible);
        shieldSlot.setVisible(visible);

        // Equipment items
        swordIcon.setVisible(visible);
        bowIcon.setVisible(visible);

        // Damage indicators
        swordDamageIcon.setVisible(visible);
        bowDamageIcon.setVisible(visible);
        swordDamageLabel.setVisible(visible);
        bowDamageLabel.setVisible(visible);

        // General inventory slots
        for (BaseActor slot : inventorySlots) {
            slot.setVisible(visible);
        }
    }

    public boolean isInventoryVisible() {
        return isVisible;
    }

    public boolean isGameFrozen() {
        return gameFrozen;
    }

    public void updateDamage(int swordDamage, int bowDamage) {
        this.swordDamage = swordDamage;
        this.bowDamage = bowDamage;
        swordDamageLabel.setText(String.valueOf(swordDamage));
        bowDamageLabel.setText(String.valueOf(bowDamage));
    }

    // Method to add item to general inventory (for future use)
    public boolean addItemToInventory(String itemTexture) {
        for (int i = 0; i < TOTAL_INVENTORY_SLOTS; i++) {
            // Check if slot is empty (no item actor attached)
            // This is a placeholder for future item management system
            // For now, just return true
        }
        return false; // Inventory full
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Drawing is handled by the individual actors
        super.draw(batch, parentAlpha);
    }
}
