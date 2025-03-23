package com.badlogic.savethebill.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.savethebill.BaseActor;
import com.badlogic.savethebill.BaseGame;
import com.badlogic.savethebill.characters.MainCharacter;
import com.badlogic.savethebill.objects.ChristmasTree;

import java.util.Random;
import java.util.Stack;

public class LevelScreen2 extends BaseScreen {
    private MainCharacter mainCharacter;
    private boolean win;
    private boolean gameOver;
    private BaseActor youWinMessage;
    private BaseActor continueMessage;
    private Rectangle worldBounds;

    private static final int MAZE_WIDTH = 41;  // Number of cells in width (2000 / 48 ≈ 41)
    private static final int MAZE_HEIGHT = 41; // Number of cells in height
    private static final int CELL_SIZE = 48;   // Size of each cell in pixels

    private boolean[][] maze; // Maze grid: true = path, false = wall (Christmas tree)

    public void initialize() {
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

        // Вхід (нижній лівий край), вихід (верхній правий край)
        int entranceX = 0;
        int entranceY = 1;
        int exitX = MAZE_WIDTH - 1;
        int exitY = MAZE_HEIGHT - 2;

        // Переконайтеся, що клітинки входу та виходу є прохідними
        maze[entranceX][entranceY] = true;
        maze[exitX][exitY] = true;

        // Герой з'являється біля входу
        mainCharacter = new MainCharacter(entranceX * CELL_SIZE, entranceY * CELL_SIZE, mainStage);

        win = false;
        gameOver = false;
        youWinMessage = null;
        continueMessage = null;
    }

    public void update(float dt) {
        // Перевірка зіткнень з ялинками
        for (BaseActor christmasTreeActor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree"))
            mainCharacter.preventOverlap(christmasTreeActor);

        // Перевірка виходу за праву межу
        if (mainCharacter.getX() + mainCharacter.getWidth() >= this.worldBounds.width && !win && !gameOver) {
            win = true;

            // Створення повідомлення "YOU WIN"
            youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y + 50);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.fadeIn(1));

            // Повідомлення продовження
            continueMessage = new BaseActor(0, 0, mainStage);
            continueMessage.loadTexture("message-continue.png");
            continueMessage.centerAtPosition(mainStage.getCamera().position.x, mainStage.getCamera().position.y - 50);
            continueMessage.setOpacity(0);
            continueMessage.addAction(Actions.delay(1));
            continueMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }

        // Обробка переходу на новий рівень
        if (win && Gdx.input.isKeyPressed(Input.Keys.C)) {
            BaseGame.setActiveScreen(new LevelScreen());
        }
    }
    // Maze generation using Depth-FirstSearch (DFS)
    private void generateMaze() {
        maze = new boolean[MAZE_WIDTH][MAZE_HEIGHT];
        Random random = new Random();

        // Ініціалізація всіх клітинок як стін
        for (int x = 0; x < MAZE_WIDTH; x++) {
            for (int y = 0; y < MAZE_HEIGHT; y++) {
                maze[x][y] = false;
            }
        }

        Stack<int[]> stack = new Stack<>();
        // Починаємо з клітинки, яка буде входом (0, 1)
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

        // Забезпечення виходу (верхній правий кут)
        int exitX = MAZE_WIDTH - 1;
        int exitY = MAZE_HEIGHT - 2;
        if (!maze[exitX][exitY]) {
            // З'єднання виходу з найближчою клітинкою
            maze[exitX][exitY] = true;
            maze[exitX - 1][exitY] = true;
        }
    }

    // Helper method to shuffle an array
    private void shuffleArray(int[] array, Random random) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    // Helper method to remove a tree at a specific grid position
    private void removeTreeAt(int gridX, int gridY) {
        for (BaseActor actor : BaseActor.getList(mainStage, "com.badlogic.savethebill.objects.ChristmasTree")) {
            if ((int) (actor.getX() / CELL_SIZE) == gridX && (int) (actor.getY() / CELL_SIZE) == gridY) {
                actor.remove(); // Remove the tree from the stage
                break;
            }
        }
    }
}
