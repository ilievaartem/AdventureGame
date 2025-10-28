package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.GameSettings;
import com.badlogic.savethebill.SaveManager;
import com.badlogic.savethebill.characters.Flyer;
import com.badlogic.savethebill.characters.Hero;
import com.badlogic.savethebill.characters.NPC;
import com.badlogic.savethebill.objects.Arrow;
import com.badlogic.savethebill.objects.Bush;
import com.badlogic.savethebill.objects.Coin;
import com.badlogic.savethebill.objects.SmallRock;
import com.badlogic.savethebill.objects.Solid;
import com.badlogic.savethebill.objects.Sword;
import com.badlogic.savethebill.objects.Treasure;
import com.badlogic.savethebill.visualelements.ControlHUD;
import com.badlogic.savethebill.visualelements.DialogBox;
import com.badlogic.savethebill.visualelements.InventoryHUD;
import com.badlogic.savethebill.visualelements.MiniMap;
import com.badlogic.savethebill.visualelements.ShopArrow;
import com.badlogic.savethebill.visualelements.ShopHeart;
import com.badlogic.savethebill.visualelements.Smoke;
import com.badlogic.savethebill.visualelements.TilemapActor;

public class LevelScreen extends BaseScreen {
    Hero hero;
    Sword sword;

    int health;
    int coins;
    int arrows;
    boolean gameOver;
    Label messageLabel;
    DialogBox dialogBox;
    BaseActor keyEIcon;
    boolean isNearShopItem;

    Treasure treasure;
    ShopHeart shopHeart;
    ShopArrow shopArrow;

    private boolean isFullscreen = true;
    private boolean treasureOpened = false;
    private InventoryHUD inventoryHUD;
    private ControlHUD controlHUD;
    // private MiniMap miniMap;
    private Sound flyerDeath;
    private Sound coinPickup;
    private Sound itemPurchase;
    private Sound damageSound;
    private Sound meleeSound;
    private Sound shootSound;

    private boolean isNearExit = false;
    private boolean isShowingExitDialog = false;
    private float exitDialogTimer = 0f;
    private GameSettings gameSettings;

    private java.util.Set<String> destroyedObjects = new java.util.HashSet<>();
    private String savedDestroyedObjects = "";
    private boolean loadFromSave = false;
    private float savedHeroX = -1;
    private float savedHeroY = -1;

    public LevelScreen() {
        this.health = 3;
        this.coins = 5;
        this.arrows = 3;
    }

    public LevelScreen(int health, int coins, int arrows) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
    }

    public LevelScreen(int health, int coins, int arrows, String destroyedObjects,
                      boolean treasureOpened, float heroX, float heroY) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
        this.treasureOpened = treasureOpened;
        this.savedDestroyedObjects = destroyedObjects;
        this.loadFromSave = true;
        this.savedHeroX = heroX;
        this.savedHeroY = heroY;

        // Parse destroyed objects
        if (destroyedObjects != null && !destroyedObjects.isEmpty()) {
            String[] objects = destroyedObjects.split(",");
            for (String obj : objects) {
                if (!obj.trim().isEmpty()) {
                    this.destroyedObjects.add(obj.trim());
                }
            }
        }
    }

    public void initialize() {
        gameSettings = GameSettings.getInstance();

        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        TilemapActor tma;
        try {
            tma = new TilemapActor("map2.tmx", mainStage);
            System.out.println("Successfully loaded map2.tmx");
        } catch (Exception e) {
            System.err.println("Error loading map2.tmx: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Falling back to map1.tmx");
            tma = new TilemapActor("map1.tmx", mainStage);
        }

        for (MapObject obj : tma.getRectangleList("Solid")) {
            MapProperties props = obj.getProperties();
            new Solid((float) props.get("x"), (float) props.get("y"),
                (float) props.get("width"), (float) props.get("height"),
                mainStage);
        }

        MapObject startPoint = tma.getRectangleList("Start").get(0);
        MapProperties startProps = startPoint.getProperties();
        hero = new Hero((float) startProps.get("x"), (float) startProps.get("y"), mainStage);

        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);

        for (MapObject obj : tma.getTileList("Bush")) {
            MapProperties props = obj.getProperties();
            new Bush((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        for (MapObject obj : tma.getTileList("Rock")) {
            MapProperties props = obj.getProperties();
            new SmallRock((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        for (MapObject obj : tma.getTileList("Coin")) {
            MapProperties props = obj.getProperties();
            new Coin((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        MapObject treasureTile = tma.getTileList("Treasure").get(0);
        MapProperties treasureProps = treasureTile.getProperties();
        treasure = new Treasure((float) treasureProps.get("x"), (float) treasureProps.get("y"), mainStage);

        gameOver = false;

        inventoryHUD = new InventoryHUD(uiStage, health, coins, arrows);
        controlHUD = new ControlHUD(uiStage, LevelScreen.class, this);

        // Minimap is commented out - not displayed but class is kept
        // miniMap = new MiniMap(uiStage, mainStage, hero);

        messageLabel = new Label("...", BaseGame.labelStyle);
        messageLabel.setVisible(false);

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.80f);
        dialogBox.alignCenter();
        dialogBox.setVisible(true);
        dialogBox.setText("To help everyone, you need to prepare stronger and go down the passage");
        dialogBox.addAction(Actions.sequence(Actions.delay(5f), Actions.fadeOut(1f), Actions.visible(false)));

        keyEIcon = new BaseActor(0, 0, uiStage);
        keyEIcon.loadTexture("key-E.png");
        keyEIcon.setSize(32, 32);
        keyEIcon.setVisible(false);

        dialogBox.addActor(keyEIcon);
        keyEIcon.setPosition(dialogBox.getWidth() - keyEIcon.getWidth(), 0);

        uiTable.pad(20);
        uiTable.add(messageLabel).colspan(8).expandX().expandY();
        uiTable.row();
        uiTable.add(dialogBox).expandX().bottom().padBottom(60);

        for (MapObject obj : tma.getTileList("Flyer")) {
            MapProperties props = obj.getProperties();
            new Flyer((float) props.get("x"), (float) props.get("y"), mainStage);
        }

        for (MapObject obj : tma.getTileList("NPC")) {
            MapProperties props = obj.getProperties();
            NPC s = new NPC((float) props.get("x"), (float) props.get("y"), mainStage);
            s.setID((String) props.get("id"));
            s.setText((String) props.get("text"));
        }

        MapObject shopHeartTile = tma.getTileList("ShopHeart").get(0);
        MapProperties shopHeartProps = shopHeartTile.getProperties();
        shopHeart = new ShopHeart((float) shopHeartProps.get("x"), (float) shopHeartProps.get("y"), mainStage);

        MapObject shopArrowTile = tma.getTileList("ShopArrow").get(0);
        MapProperties shopArrowProps = shopArrowTile.getProperties();
        shopArrow = new ShopArrow((float) shopArrowProps.get("x"), (float) shopArrowProps.get("y"), mainStage);

        hero.toFront();

        flyerDeath = Gdx.audio.newSound(Gdx.files.internal("Flyer_Death.ogg"));
        coinPickup = Gdx.audio.newSound(Gdx.files.internal("Ring_Inventory.ogg"));
        itemPurchase = Gdx.audio.newSound(Gdx.files.internal("Sell_Buy_Item.ogg"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("Damage_Character.ogg"));
        meleeSound = Gdx.audio.newSound(Gdx.files.internal("Melee_Sound.ogg"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot_2.ogg"));

        controlHUD.initializeLevelMusic("Music_Peaceful_Village.ogg");

        if (loadFromSave) {
            restoreWorldState();
        }
    }

    private void restoreWorldState() {
        if (savedHeroX >= 0 && savedHeroY >= 0) {
            hero.setPosition(savedHeroX, savedHeroY);
        }

        for (String destroyedId : destroyedObjects) {
            if (destroyedId.startsWith("bush_")) {
                String[] parts = destroyedId.replace("bush_", "").split("_");
                if (parts.length == 2) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        removeObjectAt(x, y, "com.badlogic.savethebill.objects.Bush");
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing bush coordinates: " + destroyedId);
                    }
                }
            } else if (destroyedId.startsWith("flyer_")) {
                String[] parts = destroyedId.replace("flyer_", "").split("_");
                if (parts.length == 2) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        removeObjectAt(x, y, "com.badlogic.savethebill.characters.Flyer");
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing flyer coordinates: " + destroyedId);
                    }
                }
            } else if (destroyedId.startsWith("coin_")) {
                String[] parts = destroyedId.replace("coin_", "").split("_");
                if (parts.length == 2) {
                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        removeObjectAt(x, y, "com.badlogic.savethebill.objects.Coin");
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing coin coordinates: " + destroyedId);
                    }
                }
            }
        }

        if (treasureOpened) {
            Animation<TextureRegion> openAnimation = treasure.loadTexture("open-treasure-chest.png");
            treasure.setAnimation(openAnimation);
        }
    }

    private void removeObjectAt(int x, int y, String className) {
        for (BaseActor actor : BaseActor.getList(mainStage, className)) {
            if (Math.abs(actor.getX() - x) < 5 && Math.abs(actor.getY() - y) < 5) {
                actor.remove();
                break;
            }
        }
    }

    public void update(float dt) {
        inventoryHUD.update(health, coins, arrows);

        if (!gameOver) {
            if (!sword.isVisible()) {
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

                if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                    swingSword();
                }

                if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
                    shootArrow();
                }
            }

            for (BaseActor solid : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Solid")) {
                hero.preventOverlap(solid);

                for (BaseActor flyer : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                    if (flyer.overlaps(solid)) {
                        flyer.preventOverlap(solid);
                        flyer.setMotionAngle(flyer.getMotionAngle() + 180);
                    }
                }
            }

            if (sword.isVisible()) {
                for (BaseActor bush : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Bush")) {
                    if (sword.overlaps(bush)) {
                        String bushId = "bush_" + (int)bush.getX() + "_" + (int)bush.getY();
                        destroyedObjects.add(bushId);
                        bush.remove();
                    }
                }

                for (BaseActor flyer : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                    if (sword.overlaps(flyer)) {
                        String flyerId = "flyer_" + (int)flyer.getX() + "_" + (int)flyer.getY();
                        destroyedObjects.add(flyerId);
                        flyer.remove();
                        Coin coin = new Coin(0, 0, mainStage);
                        coin.centerAtActor(flyer);
                        Smoke smoke = new Smoke(0, 0, mainStage);
                        smoke.centerAtActor(flyer);
                        if (!controlHUD.isMuted()) {
                            flyerDeath.play(controlHUD.getEffectVolume());
                        }
                    }
                }
            }

            for (BaseActor coin : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Coin")) {
                if (hero.overlaps(coin)) {
                    String coinId = "coin_" + (int)coin.getX() + "_" + (int)coin.getY();
                    destroyedObjects.add(coinId);
                    coin.remove();
                    coins++;
                    if (!controlHUD.isMuted()) {
                        coinPickup.play(controlHUD.getEffectVolume());
                    }
                }
            }

            for (BaseActor flyer : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                if (hero.overlaps(flyer)) {
                    hero.preventOverlap(flyer);
                    flyer.setMotionAngle(flyer.getMotionAngle() + 180);
                    Vector2 heroPosition = new Vector2(hero.getX(), hero.getY());
                    Vector2 flyerPosition = new Vector2(flyer.getX(), flyer.getY());
                    Vector2 hitVector = heroPosition.sub(flyerPosition);
                    hero.setMotionAngle(hitVector.angle());
                    hero.setSpeed(100);
                    health--;
                    hero.clearActions();
                    hero.addAction(Actions.sequence(
                        Actions.color(Color.RED, 0.2f),
                        Actions.color(Color.WHITE, 0.2f)
                    ));
                    if (!controlHUD.isMuted()) {
                        damageSound.play(controlHUD.getEffectVolume());
                    }
                }
            }

            boolean nearShopHeart = hero.isWithinDistance(4, shopHeart);
            boolean nearShopArrow = hero.isWithinDistance(4, shopArrow);
            if ((nearShopHeart || nearShopArrow) && !isNearShopItem && !isShowingExitDialog) {
                dialogBox.setBackgroundColor(Color.TAN);
                dialogBox.setText("To buy item, press E");
                dialogBox.setVisible(true);
                keyEIcon.setVisible(true);
                isNearShopItem = true;
            } else if (!(nearShopHeart || nearShopArrow) && isNearShopItem) {
                dialogBox.setText(" ");
                dialogBox.setVisible(false);
                keyEIcon.setVisible(false);
                isNearShopItem = false;
            }

            for (BaseActor npcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.NPC")) {
                NPC npc = (NPC) npcActor;

                hero.preventOverlap(npc);
                boolean nearby = hero.isWithinDistance(4, npc);

                if (nearby && !npc.isViewing() && !isNearShopItem && !isShowingExitDialog) {
                    if (npc.getID().equals("Gatekeeper")) {
                        int flyerCount = BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Flyer");
                        String message = "Destroy the flyers and you can have the treasure. ";
                        if (flyerCount > 1)
                            message += "There are " + flyerCount + " left.";
                        else if (flyerCount == 1)
                            message += "There is " + flyerCount + " left.";
                        else {
                            message += "It is yours!";
                            npc.addAction(Actions.fadeOut(5.0f));
                            npc.addAction(Actions.after(Actions.moveBy(-10000, -10000)));
                        }
                        dialogBox.setText(message);
                    } else {
                        dialogBox.setText(npc.getText());
                    }
                    dialogBox.setVisible(true);
                    npc.setViewing(true);
                }

                if (npc.isViewing() && !nearby) {
                    dialogBox.setText(" ");
                    dialogBox.setVisible(false);
                    npc.setViewing(false);
                }
            }

            if (!treasureOpened && hero.overlaps(treasure)) {
                Animation<TextureRegion> openAnimation = treasure.loadTexture("open-treasure-chest.png");
                treasure.setAnimation(openAnimation);
                float originalWidth = treasure.getWidth();
                float originalHeight = treasure.getHeight();
                float originalX = treasure.getX();
                float originalY = treasure.getY();
                treasure.setSize(originalWidth, originalHeight);
                treasure.setPosition(originalX, originalY);
                health += 1;
                coins += 5;
                arrows += 3;
                treasureOpened = true;
                if (!controlHUD.isMuted()) {
                    coinPickup.play(controlHUD.getEffectVolume());
                }
            }

            if (hero.getY() <= 50 && !treasureOpened) {
                System.out.println("Hero Y: " + hero.getY() + ", TreasureOpened: " + treasureOpened);
                if (!isNearExit) {
                    dialogBox.clearActions();
                    dialogBox.setText("You cannot go further unprepared");
                    dialogBox.setVisible(true);
                    dialogBox.setOpacity(1f);
                    isNearExit = true;
                    isShowingExitDialog = true;
                    exitDialogTimer = 0f;
                    System.out.println("Exit dialog shown");
                }
            } else {
                if (isNearExit) {
                    dialogBox.setText(" ");
                    dialogBox.setVisible(false);
                    isNearExit = false;
                    isShowingExitDialog = false;
                    System.out.println("Exit dialog hidden");
                }
            }

            if (isShowingExitDialog) {
                exitDialogTimer += dt;
                if (exitDialogTimer >= 3.0f) {
                    dialogBox.setText(" ");
                    dialogBox.setVisible(false);
                    isShowingExitDialog = false;
                    System.out.println("Exit dialog auto-hidden after 3 seconds");
                }
            }

            if (treasureOpened && hero.getY() <= 0) {
                controlHUD.dispose();
                SaveManager.getInstance().saveGameWithFullState(2, health, coins, arrows,
                    getDestroyedObjects(), treasureOpened, hero.getX(), hero.getY());
                BaseGame.setActiveScreen(new LevelScreen2(health, coins, arrows));
            }

            if (health <= 0) {
                messageLabel.setText("Game over...");
                messageLabel.setColor(Color.RED);
                messageLabel.setFontScale(2);
                messageLabel.setVisible(true);
                hero.remove();
                gameOver = true;
            }

            for (BaseActor arrow : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Arrow")) {
                for (BaseActor flyer : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                    if (arrow.overlaps(flyer)) {
                        flyer.remove();
                        arrow.remove();
                        Coin coin = new Coin(0, 0, mainStage);
                        coin.centerAtActor(flyer);
                        Smoke smoke = new Smoke(0, 0, mainStage);
                        smoke.centerAtActor(flyer);
                        if (!controlHUD.isMuted()) {
                            flyerDeath.play(controlHUD.getEffectVolume());
                        }
                    }
                }

                for (BaseActor solid : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Solid")) {
                    if (arrow.overlaps(solid)) {
                        arrow.preventOverlap(solid);
                        arrow.setSpeed(0);
                        arrow.addAction(Actions.fadeOut(0.5f));
                        arrow.addAction(Actions.after(Actions.removeActor()));
                    }
                }
            }
        }
    }

    public void swingSword() {
        if (sword.isVisible())
            return;

        hero.setSpeed(0);

        float facingAngle = hero.getFacingAngle();

        Vector2 offset = new Vector2();
        if (facingAngle == 0)
            offset.set(0.50f, 0.20f);
        else if (facingAngle == 90)
            offset.set(0.65f, 0.50f);
        else if (facingAngle == 180)
            offset.set(0.40f, 0.20f);
        else
            offset.set(0.25f, 0.20f);

        sword.setPosition(hero.getX(), hero.getY());
        sword.moveBy(offset.x * hero.getWidth(), offset.y * hero.getHeight());

        float swordArc = 90;
        sword.setRotation(facingAngle - swordArc / 2);
        sword.setOriginX(0);

        sword.setVisible(true);
        sword.addAction(Actions.rotateBy(swordArc, 0.25f));
        sword.addAction(Actions.after(Actions.visible(false)));

        if (facingAngle == 90 || facingAngle == 180)
            hero.toFront();
        else
            sword.toFront();

        if (meleeSound != null && controlHUD != null && !controlHUD.isMuted()) {
            meleeSound.play(controlHUD.getEffectVolume());
        }
    }

    public void shootArrow() {
        if (arrows <= 0)
            return;

        arrows--;

        Arrow arrow = new Arrow(0, 0, mainStage);
        arrow.centerAtActor(hero);
        arrow.setRotation(hero.getFacingAngle());
        arrow.setMotionAngle(hero.getFacingAngle());

        if (shootSound != null && controlHUD != null && !controlHUD.isMuted()) {
            shootSound.play(controlHUD.getEffectVolume());
        }
    }

    public boolean keyDown(int keycode) {
        if (gameOver)
            return false;

        if (keycode == Keys.ESCAPE) {
            BillGame.setActiveScreen(new PauseScreen(this, LevelScreen.class, controlHUD.isMuted()));
            return true;
        }

        if (keycode == Keys.F) {
            if (isFullscreen) {
                Gdx.graphics.setWindowedMode(800, 600);
                isFullscreen = false;
            } else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                isFullscreen = true;
            }
            return true;
        }

        if (keycode == Keys.E) {
            boolean purchased = false;
            if (hero.overlaps(shopHeart) && coins >= 3) {
                coins -= 3;
                health += 1;
                purchased = true;
            }

            if (hero.overlaps(shopArrow) && coins >= 4) {
                coins -= 4;
                arrows += 3;
                purchased = true;
            }

            if (purchased && !controlHUD.isMuted()) {
                itemPurchase.play(controlHUD.getEffectVolume());
            }

            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        super.dispose();
        controlHUD.dispose();
        if (flyerDeath != null) {
            flyerDeath.dispose();
        }
        if (coinPickup != null) {
            coinPickup.dispose();
        }
        if (itemPurchase != null) {
            itemPurchase.dispose();
        }
        if (damageSound != null) {
            damageSound.dispose();
        }
        if (meleeSound != null) {
            meleeSound.dispose();
        }
        if (shootSound != null) {
            shootSound.dispose();
        }
    }

    public void updateSoundsMuteState() {
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

    public String getDestroyedObjects() {
        return String.join(",", destroyedObjects);
    }

    public boolean isTreasureOpened() {
        return treasureOpened;
    }
}
