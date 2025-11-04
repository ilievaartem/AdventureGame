package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.GameSettings;
import com.badlogic.savethebill.characters.Flyer;
import com.badlogic.savethebill.characters.Hero;
import com.badlogic.savethebill.characters.NPC;
import com.badlogic.savethebill.characters.NPCDog;
import com.badlogic.savethebill.characters.NPCHoe;
import com.badlogic.savethebill.characters.NPCVillager;
import com.badlogic.savethebill.characters.Orc;
import com.badlogic.savethebill.characters.Zoro;
import com.badlogic.savethebill.objects.Arrow;
import com.badlogic.savethebill.objects.JailBars;
import com.badlogic.savethebill.objects.Rock;
import com.badlogic.savethebill.objects.Sign;
import com.badlogic.savethebill.objects.Sword;
import com.badlogic.savethebill.visualelements.ControlHUD;
import com.badlogic.savethebill.visualelements.DialogBox;
import com.badlogic.savethebill.visualelements.InventoryHUD;
import com.badlogic.savethebill.visualelements.InventoryPanel;
import com.badlogic.savethebill.visualelements.TilemapActor;
import com.badlogic.savethebill.visualelements.Whirlpool;
import com.badlogic.savethebill.SaveManager;

public class LevelScreen3 extends BaseScreen {
    private Hero mainCharacter;
    private Sword sword;
    private int health;
    private int coins;
    private int arrows;
    private boolean win;
    private boolean gameOver;
    private DialogBox dialogBox;
    private float audioVolume;
    private Sound pickUp;
    private Sound damageSound;
    private Sound meleeSound;
    private Sound shootSound;
    private Sound hitPrisonSound;
    private Sound destroyPrisonSound;
    private Music instrumental;
    private Music windSurf;
    private float timeSinceVictory = 0;
    private InventoryHUD inventoryHUD;
    private InventoryPanel inventoryPanel;
    private ControlHUD controlHUD;
    private static final float DROP_VOLUME = 0.1f;
    private static final float ORC_DAMAGE_COOLDOWN = 1.0f;
    private float orcDamageTimer;
    private GameSettings gameSettings;
    private TextButton loadGameButton;
    private TextButton mainMenuButton;
    private boolean gameOverUICreated = false;
    private Label messageLabel;

    private java.util.Set<String> destroyedObjects = new java.util.HashSet<>();
    private boolean loadFromSave = false;
    private float savedHeroX = -1;
    private float savedHeroY = -1;
    private boolean treasureOpened = false;

    public LevelScreen3() {
        this(3, 5, 3);
    }

    public LevelScreen3(int health, int coins, int arrows) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
        this.orcDamageTimer = 0f;
    }

    public LevelScreen3(int health, int coins, int arrows, String destroyedObjects,
                       boolean treasureOpened, float heroX, float heroY) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
        this.treasureOpened = treasureOpened;
        this.loadFromSave = true;
        this.savedHeroX = heroX;
        this.savedHeroY = heroY;
        this.orcDamageTimer = 0f;

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
        TilemapActor tma = new TilemapActor("map.tmx", mainStage);

        inventoryHUD = new InventoryHUD(uiStage, health, coins, arrows);
        inventoryPanel = new InventoryPanel(uiStage);
        controlHUD = new ControlHUD(uiStage, LevelScreen3.class, this, true);

        for (MapObject obj : tma.getTileList("Zoro")) {
            MapProperties props = obj.getProperties();
            float x = (float) props.get("x");
            float y = (float) props.get("y");

            double random = Math.random();
            NPC npc;
            if (random < 0.25) {
                npc = new Zoro(x, y, mainStage);
            } else if (random < 0.5) {
                npc = new NPCVillager(x, y, mainStage);
            } else if (random < 0.75) {
                npc = new NPCHoe(x, y, mainStage);
            } else {
                npc = new NPCDog(x, y, mainStage);
            }

            if (Math.random() < 0.3) {
                JailBars jailBars = new JailBars(x, y, mainStage, npc, this);
                jailBars.centerAtActor(npc);
                jailBars.toFront();
            }
        }

        MapObject startPoint = tma.getRectangleList("Start").get(0);
        MapProperties props = startPoint.getProperties();
        mainCharacter = new Hero((float) props.get("x"), (float) props.get("y"), mainStage);

        for (MapObject obj : tma.getTileList("Orc")) {
            props = obj.getProperties();
            new Orc((float) props.get("x"), (float) props.get("y"), mainStage, mainCharacter, controlHUD);
        }
        for (MapObject obj : tma.getTileList("Rock")) {
            props = obj.getProperties();
            new Rock((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Sign")) {
            props = obj.getProperties();
            Sign s = new Sign((float) props.get("x"), (float) props.get("y"), mainStage);
            s.setText((String) props.get("message"));
        }

        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);

        win = false;
        gameOver = false;

        dialogBox = new DialogBox(0, 0, uiStage);
        dialogBox.setBackgroundColor(Color.TAN);
        dialogBox.setFontColor(Color.BROWN);
        dialogBox.setDialogSize(600, 100);
        dialogBox.setFontScale(0.80f);
        dialogBox.alignCenter();
        dialogBox.setVisible(false);

        uiTable.row();
        uiTable.add(dialogBox).colspan(8);

        pickUp = Gdx.audio.newSound(Gdx.files.internal("Power_Drain.ogg"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("Damage_Character.ogg"));
        meleeSound = Gdx.audio.newSound(Gdx.files.internal("Melee_Sound.ogg"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot_2.ogg"));
        hitPrisonSound = Gdx.audio.newSound(Gdx.files.internal("Hit_Prison.ogg"));
        destroyPrisonSound = Gdx.audio.newSound(Gdx.files.internal("Destroy_Prison.ogg"));
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Master_of_the_Feast.ogg"));
        windSurf = Gdx.audio.newMusic(Gdx.files.internal("Birds_Wind.ogg"));

        audioVolume = 1.00f;
        instrumental.setLooping(true);
        instrumental.setVolume(controlHUD.getInstrumentalVolume());
        instrumental.play();

        windSurf.setLooping(true);
        windSurf.setVolume(controlHUD.getWindVolume());
        windSurf.play();

        messageLabel = new Label("Game over...", BaseGame.labelStyle);
        messageLabel.setVisible(false);
        messageLabel.setColor(Color.RED);
        messageLabel.setFontScale(2);

        uiTable.pad(20);
        uiTable.add(messageLabel).colspan(8).expandX().expandY();
    }

    private void createGameOverUI() {
        messageLabel.setVisible(true);

        TextButton.TextButtonStyle loadGameStyle = new TextButton.TextButtonStyle(BaseGame.textButtonStyle);
        TextButton.TextButtonStyle menuStyle = new TextButton.TextButtonStyle(BaseGame.textButtonStyle);
        menuStyle.fontColor = Color.WHITE;

        loadGameButton = new TextButton("Load Save", loadGameStyle);
        loadGameButton.addListener(
            (com.badlogic.gdx.scenes.scene2d.Event e) -> {
                if (!(e instanceof com.badlogic.gdx.scenes.scene2d.InputEvent)) return false;

                com.badlogic.gdx.scenes.scene2d.InputEvent ie = (com.badlogic.gdx.scenes.scene2d.InputEvent) e;
                if (ie.getType().equals(com.badlogic.gdx.scenes.scene2d.InputEvent.Type.touchDown)) {
                    if (SaveManager.getInstance().hasSavedGame()) {
                        controlHUD.dispose();
                        SaveManager.getInstance().loadGame();
                    }
                    return true;
                }
                return false;
            }
        );

        mainMenuButton = new TextButton("Menu", menuStyle);
        mainMenuButton.addListener(
            (com.badlogic.gdx.scenes.scene2d.Event e) -> {
                if (!(e instanceof com.badlogic.gdx.scenes.scene2d.InputEvent)) return false;

                com.badlogic.gdx.scenes.scene2d.InputEvent ie = (com.badlogic.gdx.scenes.scene2d.InputEvent) e;
                if (ie.getType().equals(com.badlogic.gdx.scenes.scene2d.InputEvent.Type.touchDown)) {
                    controlHUD.dispose();
                    BaseGame.setActiveScreen(new MenuScreen());
                    return true;
                }
                return false;
            }
        );

        updateLoadGameButtonStyle();

        uiTable.row();
        uiTable.add().expandX().fillX(); // Empty spacer
        uiTable.add(loadGameButton).pad(10);
        uiTable.add(mainMenuButton).pad(10);
        uiTable.add().expandX().fillX(); // Empty spacer
    }

    private void updateLoadGameButtonStyle() {
        if (loadGameButton != null) {
            if (!SaveManager.getInstance().hasSavedGame()) {
                loadGameButton.setDisabled(true);
                loadGameButton.getStyle().fontColor = Color.GRAY;
            } else {
                loadGameButton.setDisabled(false);
                loadGameButton.getStyle().fontColor = Color.WHITE;
            }
        }
    }

    public void update(float dt) {
        inventoryHUD.update(health, coins, arrows);

        // Update inventory panel for Q key toggle
        if (inventoryPanel != null) {
            inventoryPanel.update();
        }

        // Don't update game logic if inventory is open (game frozen)
        if (inventoryPanel != null && inventoryPanel.isGameFrozen()) {
            return;
        }

        orcDamageTimer += dt;

        if (!win && !gameOver && !sword.isVisible()) {
            if (Gdx.input.isKeyPressed(Keys.W))
                mainCharacter.accelerateAtAngle(90);
            if (Gdx.input.isKeyPressed(Keys.S))
                mainCharacter.accelerateAtAngle(270);
            if (Gdx.input.isKeyPressed(Keys.A))
                mainCharacter.accelerateAtAngle(180);
            if (Gdx.input.isKeyPressed(Keys.D))
                mainCharacter.accelerateAtAngle(0);

            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
                mainCharacter.setMaxSpeed(200);
            } else {
                mainCharacter.setMaxSpeed(100);
            }

            if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                swingSword();
            }
            if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
                shootArrow();
            }
        }

        for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Rock"))
            mainCharacter.preventOverlap(rockActor);

        if (sword.isVisible()) {
            for (BaseActor jailBarsActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.JailBars")) {
                JailBars jailBars = (JailBars) jailBarsActor;
                if (sword.overlaps(jailBars)) {
                    jailBars.hit();
                }
            }

            for (BaseActor npcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.NPC")) {
                NPC npc = (NPC) npcActor;
                if (npc.getID() == null && sword.overlaps(npc) && !npc.collected && !isTrapped(npc)) {
                    npc.collected = true;
                    pickUp.play(DROP_VOLUME * audioVolume);
                    npc.clearActions();
                    npc.addAction(Actions.fadeOut(1));
                    npc.addAction(Actions.after(Actions.removeActor()));

                    Whirlpool whirl = new Whirlpool(0, 0, mainStage);
                    whirl.centerAtActor(npc);
                    whirl.setOpacity(0.25f);
                }
            }

            for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
                Orc orc = (Orc) orcActor;
                if (sword.overlaps(orc)) {
                    orc.takeDamage(2, "sword");
                }
            }

            for (BaseActor flyerActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                Flyer flyer = (Flyer) flyerActor;
                if (sword.overlaps(flyer)) {
                    flyer.takeDamage(1, "sword");

                    if (flyer.isDead()) {
                        flyer.remove();
                    }
                }
            }
        }

        for (BaseActor arrow : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Arrow")) {
            for (BaseActor npcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.NPC")) {
                NPC npc = (NPC) npcActor;
                if (npc.getID() == null && arrow.overlaps(npc) && !npc.collected && !isTrapped(npc)) {
                    npc.collected = true;
                    pickUp.play(DROP_VOLUME * audioVolume);
                    npc.clearActions();
                    npc.addAction(Actions.fadeOut(1));
                    npc.addAction(Actions.after(Actions.removeActor()));

                    Whirlpool whirl = new Whirlpool(0, 0, mainStage);
                    whirl.centerAtActor(npc);
                    whirl.setOpacity(0.25f);
                    arrow.remove();
                }
            }

            for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
                Orc orc = (Orc) orcActor;
                if (arrow.overlaps(orc)) {
                    orc.takeDamage(1, "arrow"); // Random 1-3 damage from arrow to Orc (handled in Orc class)
                    arrow.remove();
                }
            }

            for (BaseActor flyerActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Flyer")) {
                Flyer flyer = (Flyer) flyerActor;
                if (arrow.overlaps(flyer)) {
                    flyer.takeDamage(1, "arrow");
                    if (flyer.isDead()) {
                        flyer.remove();
                        arrow.remove();
                    } else {
                        arrow.remove();
                    }
                }
            }

            for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Rock")) {
                if (arrow.overlaps(rockActor)) {
                    arrow.remove();
                }
            }
        }

        // Updated Orc damage to player
        for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
            Orc orc = (Orc) orcActor;
            if (orc.isAttacking() && orc.overlaps(mainCharacter) && orcDamageTimer <= 0) {
                health--;
                orcDamageTimer = ORC_DAMAGE_COOLDOWN;
                mainCharacter.clearActions();
                mainCharacter.addAction(Actions.sequence(
                    Actions.color(Color.RED, 0.2f),
                    Actions.color(Color.WHITE, 0.2f)
                ));
                if (!controlHUD.isMuted()) {
                    damageSound.play(controlHUD.getEffectVolume());
                }

                if (health <= 0) {
                    if (!gameOverUICreated) {
                        createGameOverUI();
                        gameOverUICreated = true;
                    }
                    gameOver = true;
                    mainCharacter.remove();
                }
            }
        }

        if (BaseActor.count(mainStage, "com.badlogic.savethebill.characters.NPC") == 0 && !win) {
            win = true;
            mainCharacter.setSpeed(0);
            BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.fadeIn(1));
            System.out.println("All NPCs collected! Preparing to transition to MenuScreen.");
        }

        if (win) {
            timeSinceVictory += dt;
            if (timeSinceVictory >= 2.0f) {
                instrumental.stop();
                windSurf.stop();
                BaseGame.setActiveScreen(new MenuScreen());
                System.out.println("Transitioning to MenuScreen after 2 seconds.");
            }
        } else if (gameOver) {
            updateLoadGameButtonStyle();
        }
    }

    private boolean isTrapped(NPC npc) {
        for (BaseActor jailBarsActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.JailBars")) {
            JailBars jailBars = (JailBars) jailBarsActor;
            if (jailBars.getTrappedNPC() == npc) {
                return true;
            }
        }
        return false;
    }

    public void swingSword() {
        if (sword.isVisible())
            return;

        mainCharacter.setSpeed(0);

        float facingAngle = mainCharacter.getFacingAngle();

        Vector2 offset = new Vector2();
        if (facingAngle == 0)
            offset.set(0.50f, 0.20f);
        else if (facingAngle == 90)
            offset.set(0.65f, 0.50f);
        else if (facingAngle == 180)
            offset.set(0.40f, 0.20f);
        else
            offset.set(0.25f, 0.20f);

        sword.setPosition(mainCharacter.getX(), mainCharacter.getY());
        sword.moveBy(offset.x * mainCharacter.getWidth(), offset.y * mainCharacter.getHeight());

        float swordArc = 90;
        sword.setRotation(facingAngle - swordArc / 2);
        sword.setOriginX(0);

        sword.setVisible(true);
        sword.addAction(Actions.rotateBy(swordArc, 0.25f));
        sword.addAction(Actions.after(Actions.sequence(
            Actions.visible(false),
            Actions.run(() -> {
                for (BaseActor jailBarsActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.JailBars")) {
                    JailBars jailBars = (JailBars) jailBarsActor;
                    jailBars.resetHitThisSwing();
                }
            })
        )));

        if (facingAngle == 90 || facingAngle == 180)
            mainCharacter.toFront();
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
        arrow.centerAtActor(mainCharacter);
        arrow.setRotation(mainCharacter.getFacingAngle());
        arrow.setMotionAngle(mainCharacter.getFacingAngle());

        if (shootSound != null && controlHUD != null && !controlHUD.isMuted()) {
            shootSound.play(controlHUD.getEffectVolume());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (pickUp != null) {
            pickUp.dispose();
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
        if (hitPrisonSound != null) {
            hitPrisonSound.dispose();
        }
        if (destroyPrisonSound != null) {
            destroyPrisonSound.dispose();
        }
        if (instrumental != null) {
            instrumental.stop();
            instrumental.dispose();
        }
        if (windSurf != null) {
            windSurf.stop();
            windSurf.dispose();
        }
    }

    public void updateSoundsMuteState() {
        if (instrumental != null) {
            instrumental.setVolume(controlHUD.getInstrumentalVolume());
        }
        if (windSurf != null) {
            windSurf.setVolume(controlHUD.getWindVolume());
        }
    }

    public void updateSoundSettings() {
        if (controlHUD != null) {
            controlHUD.updateSoundSettings();
            updateSoundsMuteState();
        }
    }

    public Sound getHitPrisonSound() {
        return hitPrisonSound;
    }

    public Sound getDestroyPrisonSound() {
        return destroyPrisonSound;
    }

    public ControlHUD getControlHUD() {
        return controlHUD;
    }

    public void setMuted(boolean muted) {
        if (controlHUD != null) {
            controlHUD.setMuted(muted);
        }
    }

    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE) {
            BillGame.setActiveScreen(new PauseScreen(this, LevelScreen3.class, controlHUD.isMuted()));
            return true;
        }

        if (gameOver)
            return false;

        if (keycode == Keys.W)
            mainCharacter.accelerateAtAngle(90);
        if (keycode == Keys.S)
            mainCharacter.accelerateAtAngle(270);
        if (keycode == Keys.A)
            mainCharacter.accelerateAtAngle(180);
        if (keycode == Keys.D)
            mainCharacter.accelerateAtAngle(0);

        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) {
            mainCharacter.setMaxSpeed(200);
        } else {
            mainCharacter.setMaxSpeed(100);
        }

        if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            swingSword();
        }
        if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
            shootArrow();
        }

        return false;
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
        return mainCharacter;
    }

    public String getDestroyedObjects() {
        int remainingNPCs = BaseActor.count(mainStage, "com.badlogic.savethebill.characters.NPC");
        int remainingJailBars = BaseActor.count(mainStage, "com.badlogic.savethebill.objects.JailBars");
        int remainingOrcs = BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Orc");
        return "npcs_remaining:" + remainingNPCs + ",jailbars:" + remainingJailBars + ",orcs:" + remainingOrcs;
    }

    public boolean isTreasureOpened() {
        return win;
    }
}
