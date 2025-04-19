package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.BillGame;
import com.badlogic.savethebill.characters.MainCharacter;
import com.badlogic.savethebill.objects.ChristmasTree;
import com.badlogic.gdx.audio.Music;
import java.util.Random;
import java.util.Stack;

public class LevelScreen2 extends BaseScreen {
    private MainCharacter mainCharacter;
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

        mainCharacter = new MainCharacter(entranceX * CELL_SIZE, entranceY * CELL_SIZE, mainStage);

        win = false;
        gameOver = false;
        youWinMessage = null;
        continueMessage = null;

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
        for (BaseActor christmasTreeActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree"))
            mainCharacter.preventOverlap(christmasTreeActor);

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
            BaseGame.setActiveScreen(new LevelScreen());
        }

        mainCharacter.alignCamera();
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

    private void removeTreeAt(int gridX, int gridY) {
        for (BaseActor actor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            if ((int) (actor.getX() / CELL_SIZE) == gridX && (int) (actor.getY() / CELL_SIZE) == gridY) {
                actor.remove();
                break;
            }
        }
    }
}
