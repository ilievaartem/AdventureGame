package com.badlogic.savethebill.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.visualelements.ControlHUD;

public class RPGInventorySystem extends Actor {
    private boolean isVisible = false;
    private boolean qKeyPressed = false;
    private Stage uiStage;
    private boolean gameFrozen = false;

    private InventorySlot selectedSlot = null;
    private InventoryItem selectedItem = null;

    private BaseActor mainPanel;

    private InventorySlot weaponSlot;
    private InventorySlot rangedSlot;
    private InventorySlot shieldSlot;

    private InventorySlot[][] inventoryGrid;
    private static final int INVENTORY_ROWS = 6;
    private static final int INVENTORY_COLS = 8;

    private static final float PANEL_WIDTH = 600;
    private static final float PANEL_HEIGHT = 480;
    private static final float SLOT_SIZE = 48;

    private Label meleeDamageLabel;
    private Label rangedDamageLabel;
    private BaseActor swordDamageIcon;
    private BaseActor arrowDamageIcon;

    private int currentMeleeDamage = 0;
    private int currentRangedDamage = 0;
    private int currentShieldBlockChance = 0;

    private Sound equipSound;
    private Sound unequipSound;
    private Sound eatSound;

    private ControlHUD controlHUD;

    public interface FoodConsumeListener {
        void onFoodConsumed(int healAmount);
    }

    private FoodConsumeListener foodConsumeListener;

    public RPGInventorySystem(Stage uiStage) {
        this.uiStage = uiStage;

        createInventoryUI();
        setupInputListener();
        addStartingItems();
        loadSounds();

        uiStage.addActor(this);
    }

    public void setControlHUD(ControlHUD controlHUD) {
        this.controlHUD = controlHUD;
    }

    private void createInventoryUI() {
        mainPanel = new BaseActor(0, 0, uiStage);
        mainPanel.loadTexture("panel.png");
        mainPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        mainPanel.setVisible(false);

        float equipmentStartX = 50;
        float equipmentY = PANEL_HEIGHT - 100;

        weaponSlot = new InventorySlot(equipmentStartX, equipmentY, InventorySlot.SlotType.WEAPON);
        rangedSlot = new InventorySlot(equipmentStartX + 60, equipmentY, InventorySlot.SlotType.RANGED);
        shieldSlot = new InventorySlot(equipmentStartX + 120, equipmentY, InventorySlot.SlotType.SHIELD);

        weaponSlot.setStage(uiStage);
        rangedSlot.setStage(uiStage);
        shieldSlot.setStage(uiStage);

        inventoryGrid = new InventorySlot[INVENTORY_ROWS][INVENTORY_COLS];
        float inventoryStartX = 50;
        float inventoryStartY = PANEL_HEIGHT - 200;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                float x = inventoryStartX + col * (SLOT_SIZE + 2);
                float y = inventoryStartY - row * (SLOT_SIZE + 2);
                inventoryGrid[row][col] = new InventorySlot(x, y, InventorySlot.SlotType.GENERAL);
                inventoryGrid[row][col].setStage(uiStage);
            }
        }

        createDamageDisplay();
    }

    private void createDamageDisplay() {
        float damageX = 300;
        float damageY = PANEL_HEIGHT - 80;

        swordDamageIcon = new BaseActor(damageX, damageY, uiStage);
        swordDamageIcon.loadTexture("damage-sword.png");
        swordDamageIcon.setSize(32, 32);
        swordDamageIcon.setVisible(false);

        meleeDamageLabel = new Label("" + currentMeleeDamage, BaseGame.labelStyle);
        meleeDamageLabel.setPosition(damageX + 35, damageY + 5);
        meleeDamageLabel.setVisible(false);
        meleeDamageLabel.setColor(Color.WHITE);

        arrowDamageIcon = new BaseActor(damageX + 100, damageY, uiStage);
        arrowDamageIcon.loadTexture("damage-arrow.png");
        arrowDamageIcon.setSize(32, 32);
        arrowDamageIcon.setVisible(false);

        rangedDamageLabel = new Label("" + currentRangedDamage, BaseGame.labelStyle);
        rangedDamageLabel.setPosition(damageX + 135, damageY + 5);
        rangedDamageLabel.setVisible(false);
        rangedDamageLabel.setColor(Color.WHITE);

        uiStage.addActor(meleeDamageLabel);
        uiStage.addActor(rangedDamageLabel);
    }

    private void setupInputListener() {
        uiStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Keys.Q && !qKeyPressed) {
                    qKeyPressed = true;
                    toggleInventory();
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Keys.Q) {
                    qKeyPressed = false;
                    return true;
                }
                return false;
            }
        });
    }

    private void addStartingItems() {
        addItem(new InventoryItem("sword-inv.png", InventoryItem.ItemType.WEAPON, "Basic Sword", 2));
        addItem(new InventoryItem("sword-inv-second-level.png", InventoryItem.ItemType.WEAPON, "Steel Sword", 4));
        addItem(new InventoryItem("sword-inv-third-level.png", InventoryItem.ItemType.WEAPON, "Master Sword", 6));
        addItem(new InventoryItem("axe-inv.png", InventoryItem.ItemType.WEAPON, "Iron Axe", 3));

        addItem(new InventoryItem("bow-inv.png", InventoryItem.ItemType.RANGED, "Basic Bow", 2));
        addItem(new InventoryItem("bow-inv-second-level.png", InventoryItem.ItemType.RANGED, "Hunter Bow", 3));
        addItem(new InventoryItem("bow-inv-third-level.png", InventoryItem.ItemType.RANGED, "Master Bow", 4));

        addItem(new InventoryItem("shield-inv.png", InventoryItem.ItemType.SHIELD, "Basic Shield", 20));
        addItem(new InventoryItem("shield-inv-second-level.png", InventoryItem.ItemType.SHIELD, "Steel Shield", 35));
        addItem(new InventoryItem("shield-inv-third-level.png", InventoryItem.ItemType.SHIELD, "Master Shield", 50));

        addItem(new InventoryItem("food-inv.png", InventoryItem.ItemType.FOOD, "Bread", 1));
        addItem(new InventoryItem("food-inv-second-level.png", InventoryItem.ItemType.FOOD, "Cooked Meat", 3));
    }

    public boolean addItem(InventoryItem item) {
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                if (inventoryGrid[row][col].getItem() == null) {
                    inventoryGrid[row][col].setItem(item);
                    return true;
                }
            }
        }
        return false;
    }

    private void toggleInventory() {
        isVisible = !isVisible;
        gameFrozen = isVisible;

        mainPanel.setVisible(isVisible);
        weaponSlot.setVisible(isVisible);
        rangedSlot.setVisible(isVisible);
        shieldSlot.setVisible(isVisible);

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                inventoryGrid[row][col].setVisible(isVisible);
            }
        }

        swordDamageIcon.setVisible(isVisible);
        arrowDamageIcon.setVisible(isVisible);
        meleeDamageLabel.setVisible(isVisible);
        rangedDamageLabel.setVisible(isVisible);

        if (isVisible) {
            updateInventoryPosition();
            updateEquipmentStats();
        }
    }

    private void updateInventoryPosition() {
        float stageWidth = uiStage.getWidth();
        float stageHeight = uiStage.getHeight();

        float panelX = (stageWidth - PANEL_WIDTH) / 2;
        float panelY = (stageHeight - PANEL_HEIGHT) / 2;

        mainPanel.setPosition(panelX, panelY);

        weaponSlot.updatePosition(panelX + 50, panelY + PANEL_HEIGHT - 100);
        rangedSlot.updatePosition(panelX + 110, panelY + PANEL_HEIGHT - 100);
        shieldSlot.updatePosition(panelX + 170, panelY + PANEL_HEIGHT - 100);

        float inventoryStartX = panelX + 50;
        float inventoryStartY = panelY + PANEL_HEIGHT - 200;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                float x = inventoryStartX + col * (SLOT_SIZE + 2);
                float y = inventoryStartY - row * (SLOT_SIZE + 2);
                inventoryGrid[row][col].updatePosition(x, y);
            }
        }

        swordDamageIcon.setPosition(panelX + 300, panelY + PANEL_HEIGHT - 80);
        arrowDamageIcon.setPosition(panelX + 400, panelY + PANEL_HEIGHT - 80);
        meleeDamageLabel.setPosition(panelX + 335, panelY + PANEL_HEIGHT - 75);
        rangedDamageLabel.setPosition(panelX + 435, panelY + PANEL_HEIGHT - 75);
    }

    private void updateEquipmentStats() {
        currentMeleeDamage = 0;
        if (weaponSlot.getItem() != null) {
            currentMeleeDamage += weaponSlot.getItem().getValue();
        }

        currentRangedDamage = 0;
        if (rangedSlot.getItem() != null) {
            currentRangedDamage += Math.max(1, (int) (rangedSlot.getItem().getValue() * 0.8f));
        }

        // Updated shield system with correct block chances
        currentShieldBlockChance = 0;
        if (shieldSlot.getItem() != null) {
            String shieldName = shieldSlot.getItem().getTextureName();
            if (shieldName.equals("shield-inv.png")) {
                currentShieldBlockChance = 25;
            } else if (shieldName.equals("shield-inv-second-level.png")) {
                currentShieldBlockChance = 33;
            } else if (shieldName.equals("shield-inv-third-level.png")) {
                currentShieldBlockChance = 50;
            } else {
                currentShieldBlockChance = shieldSlot.getItem().getValue();
            }
        }

        meleeDamageLabel.setText("" + currentMeleeDamage);
        rangedDamageLabel.setText("" + currentRangedDamage);
    }

    public boolean canAttackWithWeapon() {
        return weaponSlot.getItem() != null;
    }

    public boolean canAttackWithRanged() {
        return rangedSlot.getItem() != null;
    }

    public int getCurrentWeaponDamage() {
        return currentMeleeDamage;
    }

    public int getCurrentRangedDamage() {
        return currentRangedDamage;
    }

    public int getCurrentArmor() {
        return 0;
    }

    public boolean canBlockAttack() {
        return shieldSlot.getItem() != null;
    }

    public boolean attemptBlock() {
        if (canBlockAttack()) {
            return Math.random() * 100 < currentShieldBlockChance;
        }
        return false;
    }

    public int getShieldBlockChance() {
        return currentShieldBlockChance;
    }

    public String getEquippedWeaponType() {
        if (weaponSlot.getItem() != null) {
            String name = weaponSlot.getItem().getName().toLowerCase();
            if (name.contains("axe")) return "AXE";
            return "SWORD";
        }
        return "NONE";
    }

    public String getEquippedWeaponTexture() {
        if (weaponSlot.getItem() != null) {
            String textureName = weaponSlot.getItem().getTextureName();

            if (textureName.equals("sword-inv.png")) return "sword.png";
            if (textureName.equals("sword-inv-second-level.png")) return "sword-second-level.png";
            if (textureName.equals("sword-inv-third-level.png")) return "sword-third-level.png";
            if (textureName.equals("axe-inv.png")) return "axe.png";

            if (textureName.contains("sword") && textureName.contains("second-level"))
                return "sword-second-level.png";
            if (textureName.contains("sword") && textureName.contains("third-level"))
                return "sword-third-level.png";
            if (textureName.contains("axe")) return "axe.png";

            return "sword.png";
        }
        return "sword.png";
    }

    public String getEquippedBowTexture() {
        if (rangedSlot.getItem() != null) {
            String textureName = rangedSlot.getItem().getTextureName();
            if (textureName.contains("second-level")) return "bow-second-level.png";
            if (textureName.contains("third-level")) return "bow-third-level.png";
            return "bow.png";
        }
        return "bow.png";
    }

    public boolean hasAxeEquipped() {
        return getEquippedWeaponType().equals("AXE");
    }

    public boolean isGameFrozen() {
        return gameFrozen;
    }

    public boolean isInventoryVisible() {
        return isVisible;
    }

    public void setFoodConsumeListener(FoodConsumeListener listener) {
        this.foodConsumeListener = listener;
    }

    public void update() {
        if (isVisible) {
            updateInventoryPosition();

            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
                handleItemClick();
            }

            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.RIGHT)) {
                handleFoodConsumption();
            }
        }
    }

    private void handleItemClick() {
        com.badlogic.gdx.math.Vector2 stageCoords = uiStage.screenToStageCoordinates(
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())
        );

        float mouseX = stageCoords.x;
        float mouseY = stageCoords.y;

        InventorySlot clickedSlot = getSlotAtPosition(mouseX, mouseY);

        if (clickedSlot != null) {
            if (selectedSlot == null) {
                if (clickedSlot.getItem() != null) {
                    selectedSlot = clickedSlot;
                    selectedItem = clickedSlot.getItem();
                    highlightSelectedSlot(clickedSlot, true);
                    playEquipSound();
                }
            } else {
                if (clickedSlot == selectedSlot) {
                    clearSelection();
                } else {
                    moveItemToSlot(selectedSlot, clickedSlot);
                    clearSelection();
                }
            }
        } else {
            clearSelection();
        }
    }

    private InventorySlot getSlotAtPosition(float x, float y) {
        if (weaponSlot.containsPoint(x, y)) return weaponSlot;
        if (rangedSlot.containsPoint(x, y)) return rangedSlot;
        if (shieldSlot.containsPoint(x, y)) return shieldSlot;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                if (inventoryGrid[row][col].containsPoint(x, y)) {
                    return inventoryGrid[row][col];
                }
            }
        }
        return null;
    }

    private void moveItemToSlot(InventorySlot fromSlot, InventorySlot toSlot) {
        if (fromSlot == null || toSlot == null || selectedItem == null) return;

        if (!toSlot.canAcceptItem(selectedItem)) {
            return;
        }

        InventoryItem existingItem = toSlot.getItem();

        toSlot.setItem(selectedItem);
        fromSlot.setItem(null);

        if (existingItem != null) {
            if (fromSlot.canAcceptItem(existingItem)) {
                fromSlot.setItem(existingItem);
            } else {
                InventorySlot emptySlot = findEmptySlotForItem(existingItem);
                if (emptySlot != null) {
                    emptySlot.setItem(existingItem);
                } else {
                    fromSlot.setItem(selectedItem);
                    toSlot.setItem(existingItem);
                    return;
                }
            }
        }

        updateEquipmentStats();
        playEquipSound();
    }

    private void clearSelection() {
        if (selectedSlot != null) {
            highlightSelectedSlot(selectedSlot, false);
        }
        selectedSlot = null;
        selectedItem = null;
    }

    private void highlightSelectedSlot(InventorySlot slot, boolean highlight) {
        if (slot == null || slot.getImage() == null) return;

        if (highlight) {
            slot.getImage().setColor(1.0f, 1.0f, 0.0f, 1.0f);
        } else {
            slot.getImage().setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    private InventorySlot findEmptySlotForItem(InventoryItem item) {
        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                if (inventoryGrid[row][col].getItem() == null) {
                    return inventoryGrid[row][col];
                }
            }
        }
        return null;
    }

    private void playEquipSound() {
        if (equipSound != null && controlHUD != null && !controlHUD.isMuted()) {
            equipSound.play(controlHUD.getEffectVolume());
        }
    }

    private void handleFoodConsumption() {
        com.badlogic.gdx.math.Vector2 stageCoords = uiStage.screenToStageCoordinates(
            new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())
        );

        float mouseX = stageCoords.x;
        float mouseY = stageCoords.y;

        for (int row = 0; row < INVENTORY_ROWS; row++) {
            for (int col = 0; col < INVENTORY_COLS; col++) {
                InventorySlot slot = inventoryGrid[row][col];
                if (slot.getItem() != null && slot.getItem().getType() == InventoryItem.ItemType.FOOD) {
                    if (slot.containsPoint(mouseX, mouseY)) {
                        consumeFood(slot);
                        break;
                    }
                }
            }
        }
    }

    private void consumeFood(InventorySlot slot) {
        InventoryItem food = slot.getItem();
        if (food != null && food.getType() == InventoryItem.ItemType.FOOD) {
            int healAmount = food.getValue();
            slot.setItem(null);
            playEatSound();

            if (foodConsumeListener != null) {
                foodConsumeListener.onFoodConsumed(healAmount);
            }
        }
    }

    private void loadSounds() {
        try {
            equipSound = Gdx.audio.newSound(Gdx.files.internal("Click.ogg"));
            unequipSound = Gdx.audio.newSound(Gdx.files.internal("Sell_Buy_Item.ogg"));
            eatSound = Gdx.audio.newSound(Gdx.files.internal("Food_Bite.ogg"));
        } catch (Exception e) {
            System.err.println("Error loading inventory sounds: " + e.getMessage());
        }
    }

    private void playEatSound() {
        if (eatSound != null && controlHUD != null && !controlHUD.isMuted()) {
            eatSound.play(controlHUD.getEffectVolume());
        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        update();
    }

    public void dispose() {
        if (equipSound != null) equipSound.dispose();
        if (unequipSound != null) unequipSound.dispose();
        if (eatSound != null) eatSound.dispose();
    }
}
