package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.characters.MainCharacter;
import com.badlogic.savethebill.objects.ChristmasTree;
import com.badlogic.savethebill.visualelements.LightMask;

import java.util.Random;
import java.util.Stack;

public class LevelScreen2 extends BaseScreen {
    private MainCharacter mainCharacter;
    private boolean win;
    private boolean gameOver;
    private BaseActor youWinMessage;
    private BaseActor continueMessage;
    private Rectangle worldBounds;
    private LightMask lightMask;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    private static final int MAZE_WIDTH = 41;
    private static final int MAZE_HEIGHT = 41;
    private static final int CELL_SIZE = 48;

    private boolean[][] maze;

    public void initialize() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        BaseActor grass = new BaseActor(0, 0, mainStage);
        grass.loadTexture("grass-2.png");
        grass.setSize(2000, 2000);
        BaseActor.setWorldBounds(grass);
        this.worldBounds = new Rectangle(0, 0, 2000, 2000);

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
        int lightRadius = 100;
        lightMask = new LightMask(0, 0, mainStage, lightRadius);

        mainCharacter = new MainCharacter(entranceX * CELL_SIZE, entranceY * CELL_SIZE, mainStage);

        lightMask.updatePosition(
            mainCharacter.getX() + mainCharacter.getWidth() / 2,
            mainCharacter.getY() + mainCharacter.getHeight() / 2
        );

        win = false;
        gameOver = false;
        youWinMessage = null;
        continueMessage = null;
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mainStage.act(delta);
        mainStage.draw();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        batch.begin();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        lightMask.draw(batch, 1);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }

    public void update(float dt) {
        for (BaseActor christmasTreeActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree"))
            mainCharacter.preventOverlap(christmasTreeActor);

        if (mainCharacter.getX() + mainCharacter.getWidth() >= this.worldBounds.width && !win && !gameOver) {
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
            BaseGame.setActiveScreen(new LevelScreen());
        }

        lightMask.updatePosition(
            mainCharacter.getX() + mainCharacter.getWidth() / 2,
            mainCharacter.getY() + mainCharacter.getHeight() / 2
        );

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
