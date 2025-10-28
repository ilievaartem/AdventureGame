package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.GameSettings;
import com.badlogic.savethebill.SaveManager;
import com.badlogic.savethebill.characters.IcyHeroMovement;
import com.badlogic.savethebill.characters.Spider;
import com.badlogic.savethebill.objects.Arrow;
import com.badlogic.savethebill.objects.ChristmasTree;
import com.badlogic.savethebill.objects.Sword;
import com.badlogic.savethebill.visualelements.ControlHUD;
import com.badlogic.savethebill.visualelements.InventoryHUD;
import com.badlogic.savethebill.visualelements.SmallerSmoke;

import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class LevelScreen2 extends BaseScreen {
    private IcyHeroMovement mainCharacter;
    private Sword sword;
    private int health;
    private int coins;
    private int arrows;
    private boolean win;
    private boolean gameOver;
    private Rectangle worldBounds;
    private Music instrumental;
    private Music windSurf;
    private Sound damageSound;
    private Sound spiderDeathSound;
    private Sound meleeSound;
    private Sound shootSound;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private float crowSoundTimer = 0;
    private static final float CROW_SOUND_INTERVAL = 10.0f;
    private static final int MAZE_WIDTH = 24;
    private static final int MAZE_HEIGHT = 24;
    private static final int CELL_SIZE = 48;
    private boolean[][] maze;
    private InventoryHUD inventoryHUD;
    private ControlHUD controlHUD;
    private HashMap<Spider, Float> spiderDamageCooldowns;
    private static final float DAMAGE_COOLDOWN = 1.0f;
    private GameSettings gameSettings;

    private java.util.Set<String> destroyedObjects = new java.util.HashSet<>();
    private boolean loadFromSave = false;
    private float savedHeroX = -1;
    private float savedHeroY = -1;
    private boolean treasureOpened = false;

    public LevelScreen2() {
        this(3, 5, 3);
    }

    public LevelScreen2(int health, int coins, int arrows) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
    }

    public LevelScreen2(int health, int coins, int arrows, String destroyedObjects,
                       boolean treasureOpened, float heroX, float heroY) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
        this.treasureOpened = treasureOpened;
        this.loadFromSave = true;
        this.savedHeroX = heroX;
        this.savedHeroY = heroY;

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
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("grass-2.png");
        grass.setSize(1152, 1152);
        BaseActor.setWorldBounds(1152, 1152);
        this.worldBounds = new Rectangle(0, 0, 1152, 1152);

        generateMaze();

        for (int x = 0; x < MAZE_WIDTH; x++) {
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                if (!maze[x][y]) {
                    new ChristmasTree(x * CELL_SIZE, y * CELL_SIZE, mainStage);
                }
            }
        }

        int entranceX = 0;
        int entranceY = 1;
        int exitX = MAZE_WIDTH - 1;
        int exitY = MAZE_HEIGHT - 2;

        maze[entranceX][entranceY] = true;
        maze[exitX][exitY] = true;

        mainCharacter = new IcyHeroMovement(entranceX * CELL_SIZE, entranceY * CELL_SIZE, mainStage);

        sword = new Sword(0, 0, mainStage);
        sword.setVisible(false);

        Random random = new Random();
        int spiderCount = 10;
        for (int i = 0; i < spiderCount; i++) {
            int x, y;
            do {
                x = random.nextInt(MAZE_WIDTH);
                y = random.nextInt(MAZE_HEIGHT);
            } while (!maze[x][y] || (x == entranceX && y == entranceY) || (x == exitX && y == exitY));
            new Spider(x * CELL_SIZE, y * CELL_SIZE, mainStage, mainCharacter);
        }

        win = false;
        gameOver = false;

        inventoryHUD = new InventoryHUD(uiStage, health, coins, arrows);
        controlHUD = new ControlHUD(uiStage, LevelScreen2.class, this);

        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Birds_Wind_Synth.ogg"));
        windSurf = Gdx.audio.newMusic(Gdx.files.internal("Scary_Сrow_Сaw.ogg"));
        damageSound = Gdx.audio.newSound(Gdx.files.internal("Damage_Character.ogg"));
        spiderDeathSound = Gdx.audio.newSound(Gdx.files.internal("Flyer_Death.ogg"));
        meleeSound = Gdx.audio.newSound(Gdx.files.internal("Melee_Sound.ogg"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("Shoot_2.ogg"));

        instrumental.setLooping(true);
        instrumental.setVolume(controlHUD.getInstrumentalVolume());
        instrumental.play();

        windSurf.setLooping(true);
        windSurf.setVolume(controlHUD.getWindVolume());
        windSurf.play();

        spiderDamageCooldowns = new HashMap<>();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainStage.act(delta);
        mainStage.draw();
        uiStage.act(delta);
        uiStage.draw();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(mainStage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(worldBounds.x, worldBounds.y, worldBounds.width, worldBounds.height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        if (instrumental != null) {
            instrumental.stop();
            instrumental.dispose();
        }
        if (windSurf != null) {
            windSurf.stop();
            windSurf.dispose();
        }
        if (damageSound != null) {
            damageSound.dispose();
        }
        if (spiderDeathSound != null) {
            spiderDeathSound.dispose();
        }
        if (meleeSound != null) {
            meleeSound.dispose();
        }
        if (shootSound != null) {
            shootSound.dispose();
        }
    }

    public void update(float dt) {
        inventoryHUD.update(health, coins, arrows);

        if (!win && !gameOver && !sword.isVisible()) {
            if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                swingSword();
            }
            if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
                shootArrow();
            }
        }

        for (BaseActor christmasTreeActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            mainCharacter.preventOverlap(christmasTreeActor);
            for (BaseActor spider : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Spider")) {
                spider.preventOverlap(christmasTreeActor);
            }
        }

        for (BaseActor arrow : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Arrow")) {
            for (BaseActor tree : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
                if (arrow.overlaps(tree)) {
                    arrow.remove();
                    break;
                }
            }
        }

        for (Spider spider : spiderDamageCooldowns.keySet()) {
            float cooldown = spiderDamageCooldowns.get(spider) - dt;
            if (cooldown <= 0) {
                spiderDamageCooldowns.remove(spider);
            } else {
                spiderDamageCooldowns.put(spider, cooldown);
            }
        }

        for (BaseActor spiderActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.characters.Spider")) {
            Spider spider = (Spider) spiderActor;

            if (spider.isAttacking() && spider.overlaps(mainCharacter) && !gameOver && !spiderDamageCooldowns.containsKey(spider)) {
                health--;
                spider.resetAttackCooldown();
                spiderDamageCooldowns.put(spider, DAMAGE_COOLDOWN);
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
                }
            }

            if (sword.isVisible() && spider.overlaps(sword)) {
                new SmallerSmoke(spider.getX(), spider.getY(), mainStage);
                if (!controlHUD.isMuted()) {
                    spiderDeathSound.play(controlHUD.getEffectVolume());
                }
                spider.remove();
            }

            for (BaseActor arrow : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Arrow")) {
                if (arrow.overlaps(spider)) {
                    new SmallerSmoke(spider.getX(), spider.getY(), mainStage);
                    if (!controlHUD.isMuted()) {
                        spiderDeathSound.play(controlHUD.getEffectVolume());
                    }
                    spider.remove();
                    arrow.remove();
                }
            }
        }

        controlHUD.updateMuteState(instrumental.getVolume(), windSurf.getVolume());
        if (controlHUD.getWindVolume() > 0) {
            crowSoundTimer += dt;
            if (crowSoundTimer >= CROW_SOUND_INTERVAL) {
                windSurf.play();
                windSurf.setVolume(controlHUD.getWindVolume());
                crowSoundTimer = 0;
            }
        }

        int exitX = MAZE_WIDTH - 1;
        int exitY = MAZE_HEIGHT - 2;
        float exitYPosition = exitY * CELL_SIZE;
        float mazeRightBoundary = MAZE_WIDTH * CELL_SIZE;

        System.out.println("Hero position: x=" + mainCharacter.getX() + ", y=" + mainCharacter.getY() +
            ", width=" + mainCharacter.getWidth() + ", height=" + mainCharacter.getHeight());
        System.out.println("Exit check: x + width=" + (mainCharacter.getX() + mainCharacter.getWidth()) +
            ", mazeRightBoundary=" + mazeRightBoundary +
            ", y=" + mainCharacter.getY() + ", exitYPosition=" + exitYPosition +
            ", y + height=" + (mainCharacter.getY() + mainCharacter.getHeight()) +
            ", exitYPosition + CELL_SIZE=" + (exitYPosition + CELL_SIZE));

        if (!win && !gameOver) {
            if (mainCharacter.getX() + mainCharacter.getWidth() >= mazeRightBoundary - 10 &&
                mainCharacter.getY() + mainCharacter.getHeight() >= exitYPosition &&
                mainCharacter.getY() <= exitYPosition + CELL_SIZE) {
                win = true;
                System.out.println("Win condition met! Transitioning to LevelScreen3");
                instrumental.stop();
                windSurf.stop();
                SaveManager.getInstance().saveGameWithFullState(3, health, coins, arrows,
                    getDestroyedObjects(), win, mainCharacter.getX(), mainCharacter.getY());
                BaseGame.setActiveScreen(new LevelScreen3(health, coins, arrows));
            }
        }

        mainCharacter.alignCamera();
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
        sword.addAction(Actions.after(Actions.visible(false)));

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

    private void generateMaze() {
        maze = new boolean[MAZE_WIDTH][MAZE_HEIGHT];
        Random random = new Random();

        for (int x = 0; x < MAZE_WIDTH; x++) {
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                maze[x][y] = false;
            }
        }

        Stack<int[]> stack = new Stack<>();
        int startX = 0;
        int startY = 1;
        maze[startX][startY] = true;
        stack.push(new int[]{startX, startY});

        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int x = current[0];
            int y = current[1];

            boolean hasUnvisited = false;
            int[] dirs = {0, 1, 2, 3};
            shuffleArray(dirs, random);

            for (int dir : dirs) {
                int nx = x + dx[dir] * 2;
                int ny = y + dy[dir] * 2;

                if (nx >= 0 && nx < MAZE_WIDTH && ny >= 0 && ny < MAZE_HEIGHT && !maze[nx][ny]) {
                    maze[nx][ny] = true;
                    maze[x + dx[dir]][y + dy[dir]] = true;
                    stack.push(new int[]{nx, ny});
                    hasUnvisited = true;
                    break;
                }
            }

            if (!hasUnvisited) {
                stack.pop();
            }
        }

        int exitX = MAZE_WIDTH - 1;
        int exitY = MAZE_HEIGHT - 2;
        maze[exitX][exitY] = true;
        maze[exitX - 1][exitY] = true;

        System.out.println("Exit cell open: maze[" + exitX + "][" + exitY + "] = " + maze[exitX][exitY]);
        System.out.println("Pre-exit cell open: maze[" + (exitX - 1) + "][" + exitY + "] = " + maze[exitX - 1][exitY]);
    }

    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
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

    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.ESCAPE) {
            BillGame.setActiveScreen(new PauseScreen(this, LevelScreen2.class, controlHUD.isMuted()));
            return true;
        }
        return false;
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

    public IcyHeroMovement getHero() {
        return mainCharacter;
    }

    public String getDestroyedObjects() {
        int remainingSpiders = BaseActor.count(mainStage, "com.badlogic.savethebill.characters.Spider");
        return "spiders_remaining:" + remainingSpiders;
    }

    public boolean isTreasureOpened() {
        // LevelScreen2 doesn't have treasures, but we track if level is completed
        return win;
    }
}
