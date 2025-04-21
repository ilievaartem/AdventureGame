package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.characters.IcyHeroMovement;
import com.badlogic.savethebill.objects.ChristmasTree;
import com.badlogic.savethebill.objects.Sword;
import com.badlogic.savethebill.objects.Arrow;
import com.badlogic.gdx.audio.Music;
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
    private BaseActor youWinMessage;
    private BaseActor continueMessage;
    private Rectangle worldBounds;
    private Music instrumental;
    private Music windSurf;
    private boolean isMuted = false;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private float crowSoundTimer = 0;
    private static final float INSTRUMENTAL_VOLUME = 0.1f;
    private static final float WIND_VOLUME = 0.1f;
    private static final float CROW_SOUND_INTERVAL = 10.0f;
    private static final int MAZE_WIDTH = 24;
    private static final int MAZE_HEIGHT = 24;
    private static final int CELL_SIZE = 48;
    private boolean[][] maze;
    private Label healthLabel;
    private Label coinLabel;
    private Label arrowLabel;

    public LevelScreen2() {
        this(3, 5, 3);
    }

    public LevelScreen2(int health, int coins, int arrows) {
        this.health = health;
        this.coins = coins;
        this.arrows = arrows;
    }

    public void initialize() {
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

        win = false;
        gameOver = false;
        youWinMessage = null;
        continueMessage = null;

        healthLabel = new Label(" x " + health, BaseGame.labelStyle);
        healthLabel.setColor(Color.PINK);
        coinLabel = new Label(" x " + coins, BaseGame.labelStyle);
        coinLabel.setColor(Color.GOLD);
        arrowLabel = new Label(" x " + arrows, BaseGame.labelStyle);
        arrowLabel.setColor(Color.TAN);

        BaseActor healthIcon = new BaseActor(0, 0, uiStage);
        healthIcon.loadTexture("heart-icon.png");
        BaseActor coinIcon = new BaseActor(0, 0, uiStage);
        coinIcon.loadTexture("coin-icon1.png");
        BaseActor arrowIcon = new BaseActor(0, 0, uiStage);
        arrowIcon.loadTexture("arrow-icon.png");

        ButtonStyle buttonStyle = new ButtonStyle();
        Texture buttonTex = new Texture(Gdx.files.internal("undo.png"));
        TextureRegion buttonRegion = new TextureRegion(buttonTex);
        buttonStyle.up = new TextureRegionDrawable(buttonRegion);

        Button restartButton = new Button(buttonStyle);
        restartButton.setColor(Color.CYAN);

        restartButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;

                instrumental.dispose();
                windSurf.dispose();

                BillGame.setActiveScreen(new LevelScreen2());
                return false;
            }
        );

        ButtonStyle buttonStyle2 = new ButtonStyle();
        Texture buttonTex2 = new Texture(Gdx.files.internal("audio.png"));
        Texture buttonTex2Muted = new Texture(Gdx.files.internal("no-audio.png"));
        TextureRegion buttonRegion2 = new TextureRegion(buttonTex2);
        TextureRegion buttonRegion2Muted = new TextureRegion(buttonTex2Muted);
        buttonStyle2.up = new TextureRegionDrawable(buttonRegion2);

        Button muteButton = new Button(buttonStyle2);
        muteButton.setColor(Color.CYAN);

        muteButton.addListener(
            (Event e) ->
            {
                if (!(e instanceof InputEvent) ||
                    !((InputEvent) e).getType().equals(Type.touchDown))
                    return false;

                isMuted = !isMuted;
                instrumental.setVolume(isMuted ? 0 : INSTRUMENTAL_VOLUME);
                windSurf.setVolume(isMuted ? 0 : WIND_VOLUME);

                muteButton.getStyle().up = isMuted
                    ? new TextureRegionDrawable(buttonRegion2Muted)
                    : new TextureRegionDrawable(buttonRegion2);

                return true;
            }
        );

        uiTable.pad(10);
        uiTable.add(healthIcon);
        uiTable.add(healthLabel);
        uiTable.add().expandX();
        uiTable.add(coinIcon);
        uiTable.add(coinLabel);
        uiTable.add().expandX();
        uiTable.add(arrowIcon);
        uiTable.add(arrowLabel);
        uiTable.row();
        uiTable.add().expandX().expandY();
        uiTable.add(muteButton).top();
        uiTable.add(restartButton).top();

        instrumental = Gdx.audio.newMusic(Gdx.files.internal("Birds_Wind_Synth.ogg"));
        windSurf = Gdx.audio.newMusic(Gdx.files.internal("Scary_Сrow_Сaw.ogg"));

        instrumental.setLooping(true);
        instrumental.setVolume(INSTRUMENTAL_VOLUME);
        instrumental.play();
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
        instrumental.dispose();
        windSurf.dispose();
    }

    public void update(float dt) {
        healthLabel.setText(" x " + health);
        coinLabel.setText(" x " + coins);
        arrowLabel.setText(" x " + arrows);

        if (!win && !gameOver && !sword.isVisible()) {
            if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                swingSword();
            }
            if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
                shootArrow();
            }
        }

        for (BaseActor christmasTreeActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree"))
            mainCharacter.preventOverlap(christmasTreeActor);

        if (sword.isVisible()) {
            for (BaseActor tree : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
                if (sword.overlaps(tree)) {
                    tree.remove();
                }
            }
        }

        for (BaseActor arrow : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.Arrow")) {
            for (BaseActor tree : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
                if (arrow.overlaps(tree)) {
                    tree.remove();
                    arrow.remove();
                }
            }
        }

        if (!isMuted) {
            crowSoundTimer += dt;
            if (crowSoundTimer >= CROW_SOUND_INTERVAL) {
                windSurf.play();
                windSurf.setVolume(WIND_VOLUME);
                crowSoundTimer = 0;
            }
        }

        if (mainCharacter.getX() + mainCharacter.getWidth() >= (MAZE_WIDTH - 1) * CELL_SIZE && !win && !gameOver) {
            win = true;
            youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y + 50);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.fadeIn(1));

            continueMessage = new BaseActor(0, 0, mainStage);
            continueMessage.loadTexture("message-continue.png");
            continueMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y - 50);
            continueMessage.setOpacity(0);
            continueMessage.addAction(Actions.delay(1));
            continueMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }

        if (win && Gdx.input.isKeyPressed(Input.Keys.C)) {
            instrumental.stop();
            windSurf.stop();
            BaseGame.setActiveScreen(new LevelScreen3(health, coins, arrows));
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
        if (!maze[exitX][exitY]) {
            maze[exitX][exitY] = true;
            maze[exitX - 1][exitY] = true;
        }
    }

    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
