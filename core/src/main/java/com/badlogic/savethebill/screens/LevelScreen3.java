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
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
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
import com.badlogic.savethebill.visualelements.TilemapActor;
import com.badlogic.savethebill.visualelements.Whirlpool;

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
    private Music instrumental;
    private Music windSurf;
    private float timeSinceVictory = 0;
    private InventoryHUD inventoryHUD;
    private ControlHUD controlHUD;
    private static final float DROP_VOLUME = 0.1f;

    public LevelScreen3() {
        this(3, 5, 3);
    }

    public LevelScreen3(int health, int coins, int arrows) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
    }

    public void initialize() {
        TilemapActor tma = new TilemapActor("map.tmx", mainStage);

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
                JailBars jailBars = new JailBars(x, y, mainStage, npc);
                jailBars.centerAtActor(npc);
                jailBars.toFront();
            }
        }

        for (MapObject obj : tma.getTileList("Orc")) {
            MapProperties props = obj.getProperties();
            new Orc((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Rock")) {
            MapProperties props = obj.getProperties();
            new Rock((float) props.get("x"), (float) props.get("y"), mainStage);
        }
        for (MapObject obj : tma.getTileList("Sign")) {
            MapProperties props = obj.getProperties();
            Sign s = new Sign((float) props.get("x"), (float) props.get("y"), mainStage);
            s.setText((String) props.get("message"));
        }

        MapObject startPoint = tma.getRectangleList("Start").get(0);
        MapProperties props = startPoint.getProperties();

        mainCharacter = new Hero((float) props.get("x"), (float) props.get("y"), mainStage);

        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);

        win = false;
        gameOver = false;

        inventoryHUD = new InventoryHUD(uiStage, health, coins, arrows);
        controlHUD = new ControlHUD(uiStage, LevelScreen3.class, this, true);

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
        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Master_of_the_Feast.ogg"));
        windSurf = Gdx.audio.newMusic(Gdx.files.internal("Birds_Wind.ogg"));

        audioVolume = 1.00f;
        instrumental.setLooping(true);
        instrumental.setVolume(controlHUD.getInstrumentalVolume());
        instrumental.play();
        windSurf.setLooping(true);
        windSurf.setVolume(controlHUD.getWindVolume());
        windSurf.play();
    }

    public void update(float dt) {
        inventoryHUD.update(health, coins, arrows);

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
                    orc.remove();
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
                    orc.remove();
                    arrow.remove();
                }
            }
            for (BaseActor rockActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Rock")) {
                if (arrow.overlaps(rockActor)) {
                    arrow.remove();
                }
            }
        }

        for (BaseActor npcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.NPC")) {
            NPC npc = (NPC) npcActor;
            if (npc.getID() == null && mainCharacter.overlaps(npc) && !npc.collected && !isTrapped(npc)) {
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

        controlHUD.updateNpcLabel(BaseActor.count(mainStage, "com.badlogic.savethebill.characters.NPC"));

        for (BaseActor signActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Sign")) {
            Sign sign = (Sign) signActor;
            mainCharacter.preventOverlap(sign);
            boolean nearby = mainCharacter.isWithinDistance(4, sign);
            if (nearby && !sign.isViewing()) {
                dialogBox.setText(sign.getText());
                dialogBox.setVisible(true);
                sign.setViewing(true);
            }
            if (sign.isViewing() && !nearby) {
                dialogBox.setText(" ");
                dialogBox.setVisible(false);
                sign.setViewing(false);
            }
        }

        for (BaseActor orcActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Orc")) {
            Orc orc = (Orc) orcActor;
            if (mainCharacter.overlaps(orc) && !gameOver && !win) {
                health--;
                mainCharacter.clearActions();
                mainCharacter.addAction(Actions.sequence(
                    Actions.color(Color.RED, 0.2f),
                    Actions.color(Color.WHITE, 0.2f)
                ));
                if (!controlHUD.isMuted()) {
                    damageSound.play(controlHUD.getEffectVolume());
                }
                if (health <= 0) {
                    gameOver = true;
                    mainCharacter.remove();

                    BaseActor gameOverMessage = new BaseActor(0, 0, mainStage);
                    gameOverMessage.loadTexture("game-over.png");
                    gameOverMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y);
                    gameOverMessage.setOpacity(0);
                    gameOverMessage.addAction(Actions.fadeIn(1));
                } else {
                    mainCharacter.preventOverlap(orc);
                    orc.setMotionAngle(orc.getMotionAngle() + 180);
                    Vector2 heroPosition = new Vector2(mainCharacter.getX(), mainCharacter.getY());
                    Vector2 orcPosition = new Vector2(orc.getX(), orc.getY());
                    Vector2 hitVector = heroPosition.sub(orcPosition);
                    mainCharacter.setMotionAngle(hitVector.angle());
                    mainCharacter.setSpeed(100);
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
    }

    public void shootArrow() {
        if (arrows <= 0)
            return;

        arrows--;

        Arrow arrow = new Arrow(0, 0, mainStage);
        arrow.centerAtActor(mainCharacter);
        arrow.setRotation(mainCharacter.getFacingAngle());
        arrow.setMotionAngle(mainCharacter.getFacingAngle());
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
}
