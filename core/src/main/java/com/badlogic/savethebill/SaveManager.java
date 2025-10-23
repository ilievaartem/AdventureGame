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

    public void saveGameWithState(int currentLevel, int health, int coins, int arrows,
                                 String destroyedObjects, boolean treasureOpened) {
        savePrefs.putBoolean(HAS_SAVE_KEY, true);
        savePrefs.putInteger(CURRENT_LEVEL_KEY, currentLevel);
        savePrefs.putInteger(PLAYER_HEALTH_KEY, health);
        savePrefs.putInteger(PLAYER_COINS_KEY, coins);
        savePrefs.putInteger(PLAYER_ARROWS_KEY, arrows);
        savePrefs.putString(DESTROYED_OBJECTS_KEY, destroyedObjects);
        savePrefs.putBoolean(TREASURE_OPENED_KEY, treasureOpened);
        savePrefs.putLong(SAVE_TIMESTAMP_KEY, System.currentTimeMillis());
        savePrefs.flush();
    }

    public GameSaveData loadGame() {
        if (!hasSavedGame()) {
            return null;
        }

        GameSaveData saveData = new GameSaveData();
        saveData.currentLevel = savePrefs.getInteger(CURRENT_LEVEL_KEY, 1);
        saveData.health = savePrefs.getInteger(PLAYER_HEALTH_KEY, 3);
        saveData.coins = savePrefs.getInteger(PLAYER_COINS_KEY, 0);
        saveData.arrows = savePrefs.getInteger(PLAYER_ARROWS_KEY, 0);
        saveData.destroyedObjects = savePrefs.getString(DESTROYED_OBJECTS_KEY, "");
        saveData.treasureOpened = savePrefs.getBoolean(TREASURE_OPENED_KEY, false);
        saveData.timestamp = savePrefs.getLong(SAVE_TIMESTAMP_KEY, 0);

        return saveData;
    }

    public BaseScreen createLevelScreen(GameSaveData saveData) {
        switch (saveData.currentLevel) {
            case 1:
                return new LevelScreen(saveData.health, saveData.coins, saveData.arrows);
            case 2:
                return new LevelScreen2(saveData.health, saveData.coins, saveData.arrows);
            case 3:
                return new LevelScreen3(saveData.health, saveData.coins, saveData.arrows);
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
        public String destroyedObjects = "";
        public boolean treasureOpened = false;
        public long timestamp;
    }
}
