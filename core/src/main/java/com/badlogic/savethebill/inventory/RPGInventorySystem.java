package com.badlogic.savethebill.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;

/**
 * Enhanced Standard RPG Inventory System with comprehensive drag-and-drop functionality
 * Supports equipment slots, general inventory, item management, and game state freezing
 */
public class RPGInventorySystem extends Actor {
    private boolean isVisible = false;
    private boolean qKeyPressed = false;
    private boolean iKeyPressed = false;
    private Stage uiStage;
    private boolean gameFrozen = false;

    // Drag and drop system
    private DragAndDrop dragAndDrop;

    // Background panel
    private BaseActor mainPanel;
    private BaseActor inventoryBackground;

    // Equipment slots
    private InventorySlot weaponSlot;      // Main weapon (sword)
    private InventorySlot rangedSlot;      // Ranged weapon (bow)
    private InventorySlot armorSlot;       // Armor
    private InventorySlot accessorySlot;   // Accessories/rings
    private InventorySlot helmetSlot;      // Helmet
    private InventorySlot shieldSlot;      // Shield

    // General inventory (8x6 = 48 slots for extensive RPG feel)
    private InventorySlot[][] inventoryGrid;
    private static final int INVENTORY_ROWS = 6;
    private static final int INVENTORY_COLS = 8;
    private static final int TOTAL_INVENTORY_SLOTS = INVENTORY_ROWS * INVENTORY_COLS;

    // Panel dimensions
    private static final float PANEL_WIDTH = 520;
    private static final float PANEL_HEIGHT = 480;
    private static final float SLOT_SIZE = 40;
    private static final float SLOT_SPACING = 4;

    // Equipment area (left section)
    private static final float EQUIPMENT_START_X = 30;
    private static final float EQUIPMENT_START_Y = 380;
    private static final float EQUIPMENT_SPACING = 50;

    // General inventory area (right section)
    private static final float INVENTORY_START_X = 180;
    private static final float INVENTORY_START_Y = 60;

    // Stats display area (bottom left)
    private static final float STATS_START_X = 30;
    private static final float STATS_START_Y = 200;

    // Damage indicators
    private BaseActor weaponDamageIcon;
    private BaseActor rangedDamageIcon;
    private BaseActor armorIcon;
    private Label weaponDamageLabel;
    private Label rangedDamageLabel;
    private Label armorLabel;
    private Label totalWeightLabel;
    private Label inventoryTitle;

    // Current equipment stats
    private int weaponDamage = 0; // Початкова шкода без зброї
    private int rangedDamage = 0; // Початкова шкода без зброї
    private int armor = 0;
    // Система ваги повністю видалена

    // Equipment state
    private boolean hasWeaponEquipped = false; // За замовчуванням немає зброї
    private boolean hasRangedEquipped = false; // За замовчуванням немає зброї
    private boolean hasArmorEquipped = false;

    // Sound effects
    private Sound itemPickupSound;
    private Sound itemDropSound;
    private Sound equipSound;

    // Hover tooltip
    private Label tooltipLabel;
    private BaseActor tooltipBackground;
    private boolean showingTooltip = false;

    public RPGInventorySystem(Stage uiStage) {
        this.uiStage = uiStage;
        this.dragAndDrop = new DragAndDrop();
        loadSounds();
        initializeInventory();
        uiStage.addActor(this);

        // Add input listener for closing inventory with ESC
        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.ESCAPE && isVisible) {
                    closeInventory();
                    return true;
                }
                return false;
            }
        });
    }

    private void loadSounds() {
        try {
            itemPickupSound = Gdx.audio.newSound(Gdx.files.internal("Pick_Up.ogg"));
            itemDropSound = Gdx.audio.newSound(Gdx.files.internal("Ring_Inventory.ogg"));
            equipSound = Gdx.audio.newSound(Gdx.files.internal("Sell_Buy_Item.ogg"));
        } catch (Exception e) {
            // Sounds are optional, continue without them
        }
    }

    private void initializeInventory() {
        // Create main panel centered on screen
        mainPanel = new BaseActor(0, 0, uiStage);
        mainPanel.loadTexture("panel.png");
        mainPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);

        // Center the panel on screen
        float centerX = (uiStage.getViewport().getWorldWidth() - PANEL_WIDTH) / 2f;
        float centerY = (uiStage.getViewport().getWorldHeight() - PANEL_HEIGHT) / 2f;
        mainPanel.setPosition(centerX, centerY);
        mainPanel.setVisible(false);

        // Create inventory background for better visual separation
        inventoryBackground = new BaseActor(0, 0, uiStage);
        inventoryBackground.loadTexture("dialog-translucent.png");
        inventoryBackground.setSize(PANEL_WIDTH - 40, PANEL_HEIGHT - 120);
        inventoryBackground.setPosition(centerX + 20, centerY + 20);
        inventoryBackground.setVisible(false);

        // Initialize components
        initializeEquipmentSlots();
        initializeInventoryGrid();
        initializeStatsDisplay();
        initializeTooltip();
        setupDragAndDrop();
    }

    private void initializeEquipmentSlots() {
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        // Main weapon slot (sword)
        weaponSlot = new InventorySlot(ItemType.WEAPON);
        weaponSlot.setBounds(panelX + EQUIPMENT_START_X, panelY + EQUIPMENT_START_Y, SLOT_SIZE, SLOT_SIZE);
        weaponSlot.setBackgroundTexture("slot.png");

        // Ranged weapon slot (bow)
        rangedSlot = new InventorySlot(ItemType.RANGED);
        rangedSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING, panelY + EQUIPMENT_START_Y, SLOT_SIZE, SLOT_SIZE);
        rangedSlot.setBackgroundTexture("slot.png");

        // Helmet slot
        helmetSlot = new InventorySlot(ItemType.HELMET);
        helmetSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING/2, panelY + EQUIPMENT_START_Y + 50, SLOT_SIZE, SLOT_SIZE);
        helmetSlot.setBackgroundTexture("slot.png");

        // Armor slot
        armorSlot = new InventorySlot(ItemType.ARMOR);
        armorSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING/2, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);
        armorSlot.setBackgroundTexture("slot.png");

        // Shield slot
        shieldSlot = new InventorySlot(ItemType.SHIELD);
        shieldSlot.setBounds(panelX + EQUIPMENT_START_X, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);
        shieldSlot.setBackgroundTexture("slot.png");

        // Accessory slot
        accessorySlot = new InventorySlot(ItemType.ACCESSORY);
        accessorySlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);
        accessorySlot.setBackgroundTexture("slot.png");

        // No initial equipment - player starts without weapons equipped
    }

    private void initializeInventoryGrid() {
        inventoryGrid = new InventorySlot[INVENTORY_ROWS][INVENTORY_COLS];
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                InventorySlot slot = new InventorySlot(ItemType.ANY);
                float x = panelX + INVENTORY_START_X + col * (SLOT_SIZE + SLOT_SPACING);
                float y = panelY + INVENTORY_START_Y + (INVENTORY_ROWS - 1 - row) * (SLOT_SIZE + SLOT_SPACING);
                slot.setBounds(x, y, SLOT_SIZE, SLOT_SIZE);
                slot.setBackgroundTexture("slot.png");
                inventoryGrid[row][col] = slot;
            }
        }
    }

    private void initializeStatsDisplay() {
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        // Inventory title
        inventoryTitle = new Label("INVENTORY", BaseGame.labelStyle);
        inventoryTitle.setColor(Color.WHITE);
        inventoryTitle.setFontScale(0.8f);
        inventoryTitle.setPosition(panelX + PANEL_WIDTH/2 - 50, panelY + PANEL_HEIGHT - 30);
        inventoryTitle.setVisible(false);
        uiStage.addActor(inventoryTitle);

        // Weapon damage display with icon
        weaponDamageIcon = new BaseActor(0, 0, uiStage);
        weaponDamageIcon.loadTexture("damage-sword.png");
        weaponDamageIcon.setSize(24, 24);
        weaponDamageIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y);
        weaponDamageIcon.setVisible(false);

        weaponDamageLabel = new Label("Melee: " + weaponDamage, BaseGame.labelStyle);
        weaponDamageLabel.setColor(Color.GRAY); // Сірий за замовчуванням
        weaponDamageLabel.setFontScale(0.6f);
        weaponDamageLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y + 2);
        weaponDamageLabel.setVisible(false);
        uiStage.addActor(weaponDamageLabel);

        // Ranged damage display with icon
        rangedDamageIcon = new BaseActor(0, 0, uiStage);
        rangedDamageIcon.loadTexture("damage-arrow.png");
        rangedDamageIcon.setSize(24, 24);
        rangedDamageIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y - 35);
        rangedDamageIcon.setVisible(false);

        rangedDamageLabel = new Label("Ranged: " + rangedDamage, BaseGame.labelStyle);
        rangedDamageLabel.setColor(Color.GRAY); // Сірий за замовчуванням
        rangedDamageLabel.setFontScale(0.6f);
        rangedDamageLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y - 33);
        rangedDamageLabel.setVisible(false);
        uiStage.addActor(rangedDamageLabel);

        // Armor display with icon
        armorIcon = new BaseActor(0, 0, uiStage);
        armorIcon.loadTexture("heart.png");
        armorIcon.setSize(24, 24);
        armorIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y - 70);
        armorIcon.setVisible(false);

        armorLabel = new Label("Armor: " + armor, BaseGame.labelStyle);
        armorLabel.setColor(Color.GRAY); // Сірий за замовчуванням
        armorLabel.setFontScale(0.6f);
        armorLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y - 68);
        armorLabel.setVisible(false);
        uiStage.addActor(armorLabel);

        // Прибираємо відображення ваги
        // Weight display - REMOVED
    }

    private void initializeTooltip() {
        tooltipBackground = new BaseActor(0, 0, uiStage);
        tooltipBackground.loadTexture("dialog.png");
        tooltipBackground.setSize(150, 60);
        tooltipBackground.setVisible(false);

        tooltipLabel = new Label("", BaseGame.labelStyle);
        tooltipLabel.setColor(Color.WHITE);
        tooltipLabel.setFontScale(0.5f);
        tooltipLabel.setVisible(false);
        uiStage.addActor(tooltipLabel);
    }

    private void setupDragAndDrop() {
        // Add all slots as drag and drop targets
        addSlotToDragAndDrop(weaponSlot);
        addSlotToDragAndDrop(rangedSlot);
        addSlotToDragAndDrop(armorSlot);
        addSlotToDragAndDrop(accessorySlot);
        addSlotToDragAndDrop(helmetSlot);
        addSlotToDragAndDrop(shieldSlot);

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                addSlotToDragAndDrop(inventoryGrid[row][col]);
            }
        }
    }

    private void addSlotToDragAndDrop(InventorySlot slot) {
        // Add as drag source
        dragAndDrop.addSource(new DragAndDrop.Source(slot.getSlotActor()) {
            @Override
            public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (slot.getItem() == null) return null;

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(slot.getItem());

                // Create visual representation for dragging
                BaseActor dragActor = new BaseActor(0, 0, uiStage);
                dragActor.loadTexture(slot.getItem().getTexture());
                dragActor.setSize(SLOT_SIZE * 0.9f, SLOT_SIZE * 0.9f);
                payload.setDragActor(dragActor);

                // Play pickup sound
                if (itemPickupSound != null) {
                    itemPickupSound.play(0.5f);
                }

                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    // Return item to original slot if dropped outside valid target
                    return;
                }
            }
        });

        // Add as drop target
        dragAndDrop.addTarget(new DragAndDrop.Target(slot.getSlotActor()) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                InventoryItem item = (InventoryItem) payload.getObject();
                return slot.canAcceptItem(item);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                InventoryItem draggedItem = (InventoryItem) payload.getObject();
                InventorySlot sourceSlot = (InventorySlot) source.getActor().getUserObject();

                // Handle item swapping
                InventoryItem targetItem = slot.getItem();

                if (targetItem != null && sourceSlot.canAcceptItem(targetItem)) {
                    // Swap items
                    sourceSlot.setItem(targetItem);
                    slot.setItem(draggedItem);

                    // Play equip sound for equipment slots
                    if (equipSound != null && (slot == weaponSlot || slot == rangedSlot || slot == armorSlot)) {
                        equipSound.play(0.7f);
                    }
                } else if (targetItem == null) {
                    // Move item to empty slot
                    sourceSlot.setItem(null);
                    slot.setItem(draggedItem);

                    // Play drop sound
                    if (itemDropSound != null) {
                        itemDropSound.play(0.5f);
                    }

                    // Play equip sound for equipment slots
                    if (equipSound != null && (slot == weaponSlot || slot == rangedSlot || slot == armorSlot)) {
                        equipSound.play(0.7f);
                    }
                }

                updateStats();
            }
        });

        // Add hover listener for tooltip
        slot.getSlotActor().addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (slot.getItem() != null && isVisible) {
                    showTooltip(slot.getItem(), event.getStageX(), event.getStageY());
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                hideTooltip();
            }
        });
    }

    private void showTooltip(InventoryItem item, float x, float y) {
        tooltipBackground.setPosition(x + 10, y - 50);
        tooltipBackground.setVisible(true);

        String tooltipText = item.getName() + "\n";
        if (item.getDamage() > 0) tooltipText += "Damage: " + item.getDamage() + "\n";
        if (item.getArmor() > 0) tooltipText += "Armor: " + item.getArmor();
        // Прибираємо відображення ваги з tooltip
        // tooltipText += "Weight: " + item.getWeight();

        tooltipLabel.setText(tooltipText);
        tooltipLabel.setPosition(x + 15, y - 35);
        tooltipLabel.setVisible(true);
        showingTooltip = true;
    }

    private void hideTooltip() {
        tooltipBackground.setVisible(false);
        tooltipLabel.setVisible(false);
        showingTooltip = false;
    }

    public void update() {
        handleInput();
    }

    private void handleInput() {
        boolean qKeyCurrentlyPressed = Gdx.input.isKeyPressed(Keys.Q);
        boolean iKeyCurrentlyPressed = Gdx.input.isKeyPressed(Keys.I);

        if ((qKeyCurrentlyPressed && !qKeyPressed) || (iKeyCurrentlyPressed && !iKeyPressed)) {
            toggleInventory();
        }

        qKeyPressed = qKeyCurrentlyPressed;
        iKeyPressed = iKeyCurrentlyPressed;
    }

    private void toggleInventory() {
        if (isVisible) {
            closeInventory();
        } else {
            openInventory();
        }
    }

    private void openInventory() {
        isVisible = true;
        gameFrozen = true;
        setInventoryVisible(true);

        // Center the inventory panel based on current camera position
        float centerX = (uiStage.getViewport().getWorldWidth() - PANEL_WIDTH) / 2f;
        float centerY = (uiStage.getViewport().getWorldHeight() - PANEL_HEIGHT) / 2f;

        mainPanel.setPosition(centerX, centerY);
        inventoryBackground.setPosition(centerX + 20, centerY + 20);

        // Update all UI element positions
        updateUIPositions();
    }

    private void closeInventory() {
        isVisible = false;
        gameFrozen = false;
        setInventoryVisible(false);
        hideTooltip();
    }

    private void updateUIPositions() {
        float panelX = mainPanel.getX();
        float panelY = mainPanel.getY();

        // Update title position
        inventoryTitle.setPosition(panelX + PANEL_WIDTH/2 - 50, panelY + PANEL_HEIGHT - 30);

        // Update equipment slot positions
        weaponSlot.setBounds(panelX + EQUIPMENT_START_X, panelY + EQUIPMENT_START_Y, SLOT_SIZE, SLOT_SIZE);
        rangedSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING, panelY + EQUIPMENT_START_Y, SLOT_SIZE, SLOT_SIZE);
        helmetSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING/2, panelY + EQUIPMENT_START_Y + 50, SLOT_SIZE, SLOT_SIZE);
        armorSlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING/2, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);
        shieldSlot.setBounds(panelX + EQUIPMENT_START_X, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);
        accessorySlot.setBounds(panelX + EQUIPMENT_START_X + EQUIPMENT_SPACING, panelY + EQUIPMENT_START_Y - 50, SLOT_SIZE, SLOT_SIZE);

        // Update inventory grid positions
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                float x = panelX + INVENTORY_START_X + col * (SLOT_SIZE + SLOT_SPACING);
                float y = panelY + INVENTORY_START_Y + (INVENTORY_ROWS - 1 - row) * (SLOT_SIZE + SLOT_SPACING);
                inventoryGrid[row][col].setBounds(x, y, SLOT_SIZE, SLOT_SIZE);
            }
        }

        // Update stats display positions
        weaponDamageIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y);
        weaponDamageLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y + 2);
        rangedDamageIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y - 35);
        rangedDamageLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y - 33);
        armorIcon.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y - 70);
        armorLabel.setPosition(panelX + STATS_START_X + 30, panelY + STATS_START_Y - 68);
        // Прибираємо відображення ваги
        // totalWeightLabel.setPosition(panelX + STATS_START_X, panelY + STATS_START_Y - 105);
    }

    private void setInventoryVisible(boolean visible) {
        mainPanel.setVisible(visible);
        inventoryBackground.setVisible(visible);
        inventoryTitle.setVisible(visible);

        // Equipment slots
        weaponSlot.setVisible(visible);
        rangedSlot.setVisible(visible);
        armorSlot.setVisible(visible);
        accessorySlot.setVisible(visible);
        helmetSlot.setVisible(visible);
        shieldSlot.setVisible(visible);

        // Stats display
        weaponDamageIcon.setVisible(visible);
        rangedDamageIcon.setVisible(visible);
        armorIcon.setVisible(visible);
        weaponDamageLabel.setVisible(visible);
        rangedDamageLabel.setVisible(visible);
        armorLabel.setVisible(visible);
        // Прибираємо відображення ваги
        // totalWeightLabel.setVisible(visible);

        // Inventory grid
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                inventoryGrid[row][col].setVisible(visible);
            }
        }
    }

    private void updateStats() {
        // Recalculate stats based on equipped items
        weaponDamage = weaponSlot.getItem() != null ? weaponSlot.getItem().getDamage() : 0;
        rangedDamage = rangedSlot.getItem() != null ? rangedSlot.getItem().getDamage() : 0;
        armor = 0;

        // Calculate total armor from all armor pieces
        if (armorSlot.getItem() != null) armor += armorSlot.getItem().getArmor();
        if (helmetSlot.getItem() != null) armor += helmetSlot.getItem().getArmor();
        if (shieldSlot.getItem() != null) armor += shieldSlot.getItem().getArmor();

        // Update labels
        weaponDamageLabel.setText("Melee: " + weaponDamage);
        rangedDamageLabel.setText("Ranged: " + rangedDamage);
        armorLabel.setText("Armor: " + armor);

        // Update colors based on equipment status
        weaponDamageLabel.setColor(weaponDamage > 0 ? Color.SCARLET : Color.GRAY);
        rangedDamageLabel.setColor(rangedDamage > 0 ? Color.ORANGE : Color.GRAY);
        armorLabel.setColor(armor > 0 ? Color.CYAN : Color.GRAY);

        // Update equipment status
        hasWeaponEquipped = weaponSlot.getItem() != null;
        hasRangedEquipped = rangedSlot.getItem() != null;
        hasArmorEquipped = armorSlot.getItem() != null || helmetSlot.getItem() != null || shieldSlot.getItem() != null;
    }

    // RPG Combat Methods
    public boolean canAttackWithWeapon() {
        return hasWeaponEquipped && !gameFrozen;
    }

    public boolean canAttackWithRanged() {
        return hasRangedEquipped && !gameFrozen;
    }

    public int getCurrentWeaponDamage() {
        return weaponDamage;
    }

    public int getCurrentRangedDamage() {
        return rangedDamage;
    }

    public int getCurrentArmor() {
        return armor;
    }

    public boolean isInventoryVisible() {
        return isVisible;
    }

    public boolean isGameFrozen() {
        return gameFrozen;
    }

    // Add item to first available slot
    public boolean addItem(InventoryItem item) {
        // Try to add to general inventory first
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                if (inventoryGrid[row][col].getItem() == null) {
                    inventoryGrid[row][col].setItem(item);
                    updateStats();
                    return true;
                }
            }
        }
        return false; // Inventory full
    }

    // Initialize starting items - sword and bow in inventory
    public void addStartingItems() {
        // Add basic sword and bow to inventory (not equipped)
        addItem(InventoryItem.createSword(0)); // Basic sword
        addItem(InventoryItem.createBow(0));   // Basic bow
    }

    // Quick equipment methods
    public void equipBestWeapon() {
        InventoryItem bestWeapon = null;
        int bestDamage = weaponDamage;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                InventoryItem item = inventoryGrid[row][col].getItem();
                if (item != null && item.getType() == ItemType.WEAPON && item.getDamage() > bestDamage) {
                    bestWeapon = item;
                    bestDamage = item.getDamage();
                }
            }
        }

        if (bestWeapon != null) {
            // Find the slot and swap
            for (int row = 0; row < INVENTORY_ROWS; row++) {
                for (int col = 0; col < INVENTORY_COLS; col++) {
                    if (inventoryGrid[row][col].getItem() == bestWeapon) {
                        InventoryItem oldWeapon = weaponSlot.getItem();
                        weaponSlot.setItem(bestWeapon);
                        inventoryGrid[row][col].setItem(oldWeapon);
                        updateStats();
                        return;
                    }
                }
            }
        }
    }

    // Save/Load functionality
    public String getInventoryDataString() {
        StringBuilder sb = new StringBuilder();

        // Save equipment slots
        sb.append(weaponSlot.getItem() != null ? weaponSlot.getItem().getDataString() : "empty").append(";");
        sb.append(rangedSlot.getItem() != null ? rangedSlot.getItem().getDataString() : "empty").append(";");
        sb.append(armorSlot.getItem() != null ? armorSlot.getItem().getDataString() : "empty").append(";");
        sb.append(accessorySlot.getItem() != null ? accessorySlot.getItem().getDataString() : "empty").append(";");
        sb.append(helmetSlot.getItem() != null ? helmetSlot.getItem().getDataString() : "empty").append(";");
        sb.append(shieldSlot.getItem() != null ? shieldSlot.getItem().getDataString() : "empty").append(";");

        // Save general inventory
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                InventoryItem item = inventoryGrid[row][col].getItem();
                sb.append(item != null ? item.getDataString() : "empty");
                if (row < INVENTORY_ROWS - 1 || col < INVENTORY_COLS - 1) {
                    sb.append(";");
                }
            }
        }

        return sb.toString();
    }

    public void loadInventoryData(String data) {
        if (data == null || data.isEmpty()) return;

        String[] items = data.split(";");
        int index = 0;

        // Load equipment slots
        if (index < items.length) {
            weaponSlot.setItem(InventoryItem.fromDataString(items[index++]));
        }
        if (index < items.length) {
            rangedSlot.setItem(InventoryItem.fromDataString(items[index++]));
        }
        if (index < items.length) {
            armorSlot.setItem(InventoryItem.fromDataString(items[index++]));
        }
        if (index < items.length) {
            accessorySlot.setItem(InventoryItem.fromDataString(items[index++]));
        }
        if (index < items.length) {
            helmetSlot.setItem(InventoryItem.fromDataString(items[index++]));
        }
        if (index < items.length) {
            shieldSlot.setItem(InventoryItem.fromDataString(items[index++]));
        }

        // Load general inventory
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                if (index < items.length) {
                    inventoryGrid[row][col].setItem(InventoryItem.fromDataString(items[index++]));
                }
            }
        }

        updateStats();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        if (itemPickupSound != null) itemPickupSound.dispose();
        if (itemDropSound != null) itemDropSound.dispose();
        if (equipSound != null) equipSound.dispose();
    }
}
