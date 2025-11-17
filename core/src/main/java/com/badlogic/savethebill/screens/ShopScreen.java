package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.characters.Hero;
import com.badlogic.savethebill.objects.Solid;
import com.badlogic.savethebill.visualelements.ControlHUD;
import com.badlogic.savethebill.visualelements.DialogBox;
import com.badlogic.savethebill.visualelements.InventoryHUD;
import com.badlogic.savethebill.visualelements.TilemapActor;

public class ShopScreen extends BaseScreen {
    Hero hero;

    int health;
    int coins;
    int arrows;
    DialogBox dialogBox;
    BaseActor keyEIcon;
    BaseActor outsideExit;

    private InventoryHUD inventoryHUD;
    private ControlHUD controlHUD;

    private boolean isNearExit = false;
    private LevelScreen previousLevelScreen;

    private boolean wasFullscreen = true;

    public ShopScreen(int health, int coins, int arrows, LevelScreen previousScreen) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
        this.previousLevelScreen = previousScreen;

        this.wasFullscreen = Gdx.graphics.isFullscreen();
    }

    public void initialize() {
        if (wasFullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        TilemapActor tma = new TilemapActor("shop.tmx", mainStage);

        for (MapObject obj : tma.getRectangleList("Solid")) {
            MapProperties props = obj.getProperties();
            new Solid((float) props.get("x"), (float) props.get("y"),
                (float) props.get("width"), (float) props.get("height"),
                mainStage);
        }

        MapObject startPoint = tma.getRectangleList("Start").get(0);
        MapProperties startProps = startPoint.getProperties();
        hero = new Hero((float) startProps.get("x"), (float) startProps.get("y"), mainStage);

        if (!tma.getRectangleList("Outside").isEmpty()) {
            MapObject outsideObject = tma.getRectangleList("Outside").get(0);
            MapProperties outsideProps = outsideObject.getProperties();
            outsideExit = new BaseActor((float) outsideProps.get("x"), (float) outsideProps.get("y"), mainStage);
            outsideExit.setSize((float) outsideProps.get("width"), (float) outsideProps.get("height"));
            outsideExit.setBoundaryRectangle();
            outsideExit.setVisible(false);
        }

        hero.toFront();

        inventoryHUD = new InventoryHUD(uiStage, health, coins, arrows);
        controlHUD = new ControlHUD(uiStage, ShopScreen.class, this);

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.80f);
        dialogBox.alignCenter();
        dialogBox.setVisible(true);
        dialogBox.setText("Welcome to the shop! Move around to explore");

        keyEIcon = new BaseActor(0, 0, uiStage);
        keyEIcon.loadTexture("key-E.png");
        keyEIcon.setSize(32, 32);
        keyEIcon.setVisible(false);

        dialogBox.addActor(keyEIcon);
        keyEIcon.setPosition(dialogBox.getWidth() - keyEIcon.getWidth(), 0);

        uiTable.pad(20);
        uiTable.add().colspan(8).expandX().expandY();
        uiTable.row();
        uiTable.add(dialogBox).expandX().bottom().padBottom(60);
    }

    public void update(float dt) {
        inventoryHUD.update(health, coins, arrows);

        if (Gdx.input.isKeyPressed(Keys.W))
            hero.accelerateAtAngle(90);
        if (Gdx.input.isKeyPressed(Keys.S))
            hero.accelerateAtAngle(270);
        if (Gdx.input.isKeyPressed(Keys.A))
            hero.accelerateAtAngle(180);
        if (Gdx.input.isKeyPressed(Keys.D))
            hero.accelerateAtAngle(0);

        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
            hero.setMaxSpeed(200);
        } else {
            hero.setMaxSpeed(100);
        }

        for (BaseActor solid : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Solid")) {
            hero.preventOverlap(solid);
        }

        boolean nearOutsideExit = outsideExit != null && hero.isWithinDistance(4, outsideExit);
        if (nearOutsideExit && !isNearExit) {
            dialogBox.setBackgroundColor(Color.TAN);
            dialogBox.setText("Press E to exit the shop");
            dialogBox.setVisible(true);
            keyEIcon.setVisible(true);
            isNearExit = true;
        } else if (!nearOutsideExit && isNearExit) {
            dialogBox.setText("Welcome to the shop! Move around to explore");
            dialogBox.setVisible(true);
            keyEIcon.setVisible(false);
            isNearExit = false;
        }
    }

    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            BillGame.setActiveScreen(new PauseScreen(this, ShopScreen.class, controlHUD.isMuted()));
            return true;
        }

        if (keycode == Keys.E) {
            if (outsideExit != null && hero.isWithinDistance(4, outsideExit)) {
                // Exit shop and return to LevelScreen
                exitShop();
                return true;
            }
        }
        return false;
    }

    private void exitShop() {
        controlHUD.dispose();

        if (wasFullscreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        if (previousLevelScreen != null) {
            LevelScreen newLevelScreen = new LevelScreen(
                previousLevelScreen.getHealth(),
                previousLevelScreen.getCoins(),
                previousLevelScreen.getArrows(),
                previousLevelScreen.getDestroyedObjects(),
                previousLevelScreen.isTreasureOpened(),
                previousLevelScreen.getHero().getX(),
                previousLevelScreen.getHero().getY()
            );
            BaseGame.setActiveScreen(newLevelScreen);
        } else {
            BaseGame.setActiveScreen(new LevelScreen(health, coins, arrows));
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        controlHUD.dispose();
    }

    public void updateSoundSettings() {
        if (controlHUD != null) {
            controlHUD.updateSoundSettings();
        }
    }

    public void setMuted(boolean muted) {
        if (controlHUD != null) {
            controlHUD.setMuted(muted);
        }
    }

    public ControlHUD getControlHUD() {
        return controlHUD;
    }

    public int getHealth() {
        return health;
    }

    public int getCoins() {
        return coins;
    }

    public int getArrows() {
        return arrows;
    }

    public Hero getHero() {
        return hero;
    }
}

