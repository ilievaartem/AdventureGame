package com.badlogic.savethebill;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.savethebill.screens.LevelScreen;
import com.badlogic.savethebill.screens.LevelScreen2;
import com.badlogic.savethebill.screens.LevelScreen3;
import com.badlogic.savethebill.screens.BaseScreen;

public class SaveManager {
    private static SaveManager instance;
    private Preferences savePrefs;

    private static final String SAVE_FILE = "game-save";
    private static final String HAS_SAVE_KEY = "hasSave";
    private static final String CURRENT_LEVEL_KEY = "currentLevel";
    private static final String PLAYER_HEALTH_KEY = "playerHealth";
    private static final String PLAYER_COINS_KEY = "playerCoins";
    private static final String PLAYER_ARROWS_KEY = "playerArrows";
    private static final String SAVE_TIMESTAMP_KEY = "saveTimestamp";
    private static final String DESTROYED_OBJECTS_KEY = "destroyedObjects";
    private static final String TREASURE_OPENED_KEY = "treasureOpened";
    private static final String HERO_X_KEY = "heroX";
    private static final String HERO_Y_KEY = "heroY";
    private static final String INVENTORY_DATA_KEY = "inventoryData";

    private SaveManager() {
        savePrefs = Gdx.app.getPreferences(SAVE_FILE);
    }

    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }

    public boolean hasSavedGame() {
        return savePrefs.getBoolean(HAS_SAVE_KEY, false);
    }

    public void saveGame(int currentLevel, int health, int coins, int arrows) {
        savePrefs.putBoolean(HAS_SAVE_KEY, true);
        savePrefs.putInteger(CURRENT_LEVEL_KEY, currentLevel);
        savePrefs.putInteger(PLAYER_HEALTH_KEY, health);
        savePrefs.putInteger(PLAYER_COINS_KEY, coins);
        savePrefs.putInteger(PLAYER_ARROWS_KEY, arrows);
        savePrefs.putLong(SAVE_TIMESTAMP_KEY, System.currentTimeMillis());
        savePrefs.flush();
    }

    public void saveInventoryData(String inventoryData) {
        savePrefs.putString(INVENTORY_DATA_KEY, inventoryData);
        savePrefs.flush();
    }

    public String loadInventoryData() {
        return savePrefs.getString(INVENTORY_DATA_KEY, "");
    }

    public GameSaveData loadGame() {
        if (!hasSavedGame()) {
            return null;
        }

        GameSaveData saveData = new GameSaveData();
        saveData.currentLevel = savePrefs.getInteger(CURRENT_LEVEL_KEY, 1);
        saveData.health = savePrefs.getInteger(PLAYER_HEALTH_KEY, 5);
        saveData.coins = savePrefs.getInteger(PLAYER_COINS_KEY, 0);
        saveData.arrows = savePrefs.getInteger(PLAYER_ARROWS_KEY, 10);
        saveData.timestamp = savePrefs.getLong(SAVE_TIMESTAMP_KEY, 0);
        saveData.heroX = savePrefs.getFloat(HERO_X_KEY, 0);
        saveData.heroY = savePrefs.getFloat(HERO_Y_KEY, 0);
        saveData.destroyedObjects = savePrefs.getString(DESTROYED_OBJECTS_KEY, "");
        saveData.treasureOpened = savePrefs.getBoolean(TREASURE_OPENED_KEY, false);
        saveData.inventoryData = loadInventoryData();

        return saveData;
    }

    public void saveCompleteGameState(int currentLevel, int health, int coins, int arrows,
                                     float heroX, float heroY, String destroyedObjects,
                                     boolean treasureOpened, String inventoryData) {
        savePrefs.putBoolean(HAS_SAVE_KEY, true);
        savePrefs.putInteger(CURRENT_LEVEL_KEY, currentLevel);
        savePrefs.putInteger(PLAYER_HEALTH_KEY, health);
        savePrefs.putInteger(PLAYER_COINS_KEY, coins);
        savePrefs.putInteger(PLAYER_ARROWS_KEY, arrows);
        savePrefs.putFloat(HERO_X_KEY, heroX);
        savePrefs.putFloat(HERO_Y_KEY, heroY);
        savePrefs.putString(DESTROYED_OBJECTS_KEY, destroyedObjects);
        savePrefs.putBoolean(TREASURE_OPENED_KEY, treasureOpened);
        savePrefs.putString(INVENTORY_DATA_KEY, inventoryData);
        savePrefs.putLong(SAVE_TIMESTAMP_KEY, System.currentTimeMillis());
        savePrefs.flush();
    }

    public void saveGameWithFullState(int currentLevel, int health, int coins, int arrows,
                                     String destroyedObjects, boolean treasureOpened,
                                     float heroX, float heroY) {
        saveCompleteGameState(currentLevel, health, coins, arrows, heroX, heroY,
                            destroyedObjects, treasureOpened, "");
    }

    public BaseScreen createLevelScreen(GameSaveData saveData) {
        switch (saveData.currentLevel) {
            case 1:
                return new LevelScreen(saveData.health, saveData.coins, saveData.arrows,
                                     saveData.destroyedObjects, saveData.treasureOpened,
                                     saveData.heroX, saveData.heroY);
            case 2:
                return new LevelScreen2(saveData.health, saveData.coins, saveData.arrows,
                                      saveData.destroyedObjects, saveData.treasureOpened,
                                      saveData.heroX, saveData.heroY);
            case 3:
                return new LevelScreen3(saveData.health, saveData.coins, saveData.arrows,
                                      saveData.destroyedObjects, saveData.treasureOpened,
                                      saveData.heroX, saveData.heroY);
            default:
                return new LevelScreen();
        }
    }

    public void deleteSave() {
        savePrefs.clear();
        savePrefs.flush();
    }

    public void autoSave(int currentLevel, int health, int coins, int arrows) {
        saveGame(currentLevel, health, coins, arrows);
    }

    public static class GameSaveData {
        public int currentLevel;
        public int health;
        public int coins;
        public int arrows;
        public long timestamp;
        public float heroX;
        public float heroY;
        public String destroyedObjects;
        public boolean treasureOpened;
        public String inventoryData;
    }
}
