package shaporenkoAndrew.com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import shaporenkoAndrew.com.Main;
import shaporenkoAndrew.com.gameObjects.*;
import shaporenkoAndrew.com.world.LevelGenerator;
import shaporenkoAndrew.com.world.LevelManager;

/**
 * Основной игровой экран, где происходит геймплей.
 * Функционал:
 * - Отрисовка игрового мира и всех объектов
 * - Обработка игровой логики и физики
 * - Управление состоянием игры
 * - Отображение пользовательского интерфейса
 * - Обработка пользовательского ввода
 */
public class GameScreen implements Screen {
    private final Main game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private LevelGenerator levelGenerator;
    private Player player;
    private BitmapFont font;
    private LevelManager levelManager;

    // Текстуры
    private Texture backgroundTexture;
    private Texture oreTexture;
    private Texture chestTexture;
    private Texture coinTexture;
    private Texture wallTexture;
    private Texture playerTexture;
    private Texture doorTexture;
    private Texture boxTexture;
    private Texture stackedBoxTexture;
    private Texture enemyTexture;

    // Слои игрового мира
    private Array<GameObject> backgroundLayer;
    private Array<GameObject> objectLayer;
    private Array<GameObject> wallLayer;

    private int totalCoins;
    private int collectedCoins;

    // Добавляем поля для следующего уровня
    private Array<GameObject> nextBackgroundLayer;
    private Array<GameObject> nextObjectLayer;
    private Array<GameObject> nextWallLayer;
    private LevelGenerator nextLevelGenerator;
    private int nextTotalCoins;

    private boolean isPositionSafe(float x, float y, float width, float height) {
        Rectangle playerBounds = new Rectangle(x, y, width, height);

        // Проверяем коллизии со стенами
        for (GameObject wall : wallLayer) {
            if (playerBounds.overlaps(wall.getBounds())) {
                return false;
            }
        }

        // Проверяем коллизии с объектами
        for (GameObject obj : objectLayer) {
            if ((obj instanceof Wall || obj instanceof Box || obj instanceof Ore) &&
                playerBounds.overlaps(obj.getBounds())) {
                return false;
            }
        }

        return true;
    }

    private Vector2 findSafePosition(Rectangle room) {
        float tileSize = 32;

        // Вычисляем центр комнаты
        float centerX = (room.x + room.width / 2) * tileSize;
        float centerY = (room.y + room.height / 2) * tileSize;

        // Проверяем центр комнаты
        if (isPositionSafe(centerX, centerY, tileSize, tileSize)) {
            return new Vector2(centerX, centerY);
        }

        // Если центр не подходит, ищем ближайшую безопасную позицию
        float radius = 1;
        float maxRadius = Math.min(room.width, room.height) * tileSize / 2;

        while (radius < maxRadius) {
            for (float angle = 0; angle < 360; angle += 45) {
                float x = centerX + radius * (float)Math.cos(Math.toRadians(angle));
                float y = centerY + radius * (float)Math.sin(Math.toRadians(angle));

                if (x >= room.x * tileSize && x < (room.x + room.width) * tileSize &&
                    y >= room.y * tileSize && y < (room.y + room.height) * tileSize &&
                    isPositionSafe(x, y, tileSize, tileSize)) {
                    return new Vector2(x, y);
                }
            }
            radius += tileSize / 2;
        }

        // Если безопасное место не найдено, возвращаем центр комнаты
        return new Vector2(centerX, centerY);
    }

    public GameScreen(final Main game) {
        this.game = game;
        this.font = new BitmapFont();
        this.collectedCoins = 0;
        this.levelManager = new LevelManager();

        // Инициализация всех массивов
        this.backgroundLayer = new Array<>();
        this.objectLayer = new Array<>();
        this.wallLayer = new Array<>();
        this.nextBackgroundLayer = new Array<>();
        this.nextObjectLayer = new Array<>();
        this.nextWallLayer = new Array<>();

        // Ини��иализация камеры
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.batch = new SpriteBatch();

        // Инициализация игры
        initializeGame();
        prepareNextLevel();
    }

    private void initializeGame() {
        // Загрузка текстур
        backgroundTexture = new Texture(Gdx.files.internal("floor_plain.png"));
        oreTexture = new Texture(Gdx.files.internal("gold_yellow.png"));
        chestTexture = new Texture(Gdx.files.internal("chest_golden_closed.png"));
        wallTexture = new Texture(Gdx.files.internal("wall_center.png"));
        playerTexture = new Texture(Gdx.files.internal("death_knight.png"));
        doorTexture = new Texture(Gdx.files.internal("door_open.png"));
        coinTexture = new Texture(Gdx.files.internal("coin.png"));
        boxTexture = new Texture(Gdx.files.internal("box.png"));
        stackedBoxTexture = new Texture(Gdx.files.internal("box_stacked.png"));
        enemyTexture = new Texture(Gdx.files.internal("npc_knight_yellow.png"));

        // Инициализация слоев
        backgroundLayer = new Array<>();
        objectLayer = new Array<>();
        wallLayer = new Array<>();

        // Ген��ация уровня через LevelManager
        levelGenerator = levelManager.generateLevel(
            Gdx.graphics.getWidth() * 2,
            Gdx.graphics.getHeight() * 2
        );

        // Создание объектов уровня
        levelGenerator.createGameObjects(
            backgroundLayer,
            objectLayer,
            wallLayer,
            backgroundTexture,
            wallTexture,
            oreTexture,
            chestTexture,
            coinTexture,
            doorTexture,
            boxTexture,
            stackedBoxTexture,
            false // не пропускаем дверь на первом уровне
        );

        totalCoins = levelGenerator.getTotalCoins();

        // Создание игрока в безопасной позиции
        Rectangle startRoom = levelGenerator.getRandomRoom();
        Vector2 safePosition = findSafePosition(startRoom);
        player = new Player(safePosition.x, safePosition.y, playerTexture, this);

        // Создание врагов после создания игрока
        levelGenerator.setEnemySpawnRate(2f); // Устанавливаем коэффициент спавна врагов
        levelGenerator.createEnemies(objectLayer, wallLayer, enemyTexture, player, this);
    }

    private void prepareNextLevel() {
        if (levelManager.hasNextLevel()) {
            // Генерруем следующий уровень
            nextLevelGenerator = levelManager.generateLevel(
                Gdx.graphics.getWidth() * 2,
                Gdx.graphics.getHeight() * 2
            );

            // Создаем объекты следующего уровня, не создаем дверь на последнем уровне
            nextLevelGenerator.createGameObjects(
                nextBackgroundLayer,
                nextObjectLayer,
                nextWallLayer,
                backgroundTexture,
                wallTexture,
                oreTexture,
                chestTexture,
                coinTexture,
                doorTexture,
                boxTexture,
                stackedBoxTexture,
                levelManager.getCurrentLevel() + 1 == levelManager.getTotalLevels() // пропускаем дверь на последнем уровне
            );

            nextTotalCoins = nextLevelGenerator.getTotalCoins();
        }
    }

    public void nextLevel() {
        if (levelManager.hasNextLevel()) {
            levelManager.nextLevel();

            // Сохраняем состояние игрока
            float playerHealth = player.getHealth();
            float playerStamina = player.getStamina();
            Array<Ore> playerInventory = new Array<>(player.getInventory());
            int playerCoins = player.getCoins();

            // Заменяем текущие слои на подготовленные
            backgroundLayer = nextBackgroundLayer;
            objectLayer = nextObjectLayer;
            wallLayer = nextWallLayer;
            totalCoins = nextTotalCoins;
            collectedCoins = 0;

            // Создаем новые массивы для следующего уровня
            nextBackgroundLayer = new Array<>();
            nextObjectLayer = new Array<>();
            nextWallLayer = new Array<>();

            // Создаем игрока в новой безопасной позиции
            Rectangle startRoom = nextLevelGenerator.getRandomRoom();
            Vector2 safePosition = findSafePosition(startRoom);
            player = new Player(safePosition.x, safePosition.y, playerTexture, this);

            // Восстанавливаем состояние игрока
            player.heal(playerHealth - player.getHealth());
            player.setStamina(playerStamina);
            player.setInventory(playerInventory);
            player.setCoins(playerCoins);

            // Сдаем врагов на новом уровне
            nextLevelGenerator.createEnemies(objectLayer, wallLayer, enemyTexture, player, this);

            // Подготавливаем следующий уровень
            prepareNextLevel();
        } else {
            // Создаем экран победы
            VictoryScreen victoryScreen = new VictoryScreen(game);
            // Освобождаем ресурсы текущего экрана
            dispose();
            // Устанавливаем экран победы
            Gdx.app.postRunnable(() -> {
                game.setScreen(victoryScreen);
            });
        }
    }

    private boolean isPaused = false;

    @Override
    public void render(float delta) {
        // Обработка паузы
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
        }

        if (isPaused) {
            renderPauseMenu();
            return;
        }

        // Очистка экрана
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Получаем координаты мыши в игровых координатах
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);

        // Проверяем наведение на руду
        boolean foundOre = false;
        for (GameObject obj : objectLayer) {
            if (obj instanceof Ore) {
                Rectangle bounds = obj.getBounds();
                if (bounds.contains(mousePos.x, mousePos.y)) {
                    player.setTargetOre((Ore)obj);
                    foundOre = true;
                    break;
                }
            }
        }
        if (!foundOre) {
            player.clearTargetOre();
        }

        // Обновление игрока
        player.update(delta);

        // Проверяем, не умер ли игрок
        if (player.isDead()) {
            gameOver();
            return;
        }

        // Создаем временный массив для объектов, которые нужно обновить
        Array<GameObject> objectsToUpdate = new Array<>(objectLayer);
        for (GameObject object : objectsToUpdate) {
            object.update(delta);
        }

        // Проверяем коллизии после обновления всех объектов
        player.checkCollisions(objectLayer);
        player.checkCollisions(wallLayer);

        // Обновление камеры для следования з игроком
        camera.position.set(player.getX() + player.getBounds().width/2,
                          player.getY() + player.getBounds().height/2,
                          0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Отрисовка всех слоев
        batch.begin();

        // Отрисовка фонового слоя
        for (GameObject background : backgroundLayer) {
            background.render(batch);
        }

        // Отрисовка слоя оъектов
        for (GameObject object : objectLayer) {
            object.render(batch);
        }

        // Отрисовка слоя стен
        for (GameObject wall : wallLayer) {
            wall.render(batch);
        }

        // Отрисовка игрока
        player.render(batch);

        // Отрисовка UI
        renderUI();

        batch.end();
    }

    private void renderPauseMenu() {
        batch.begin();
        float centerX = camera.position.x;
        float centerY = camera.position.y;

        // Отрисовка затемнения
        Pixmap pauseOverlay = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        pauseOverlay.setColor(0, 0, 0, 0.5f);
        pauseOverlay.fill();
        Texture pauseTexture = new Texture(pauseOverlay);
        batch.draw(pauseTexture, centerX - Gdx.graphics.getWidth()/2, centerY - Gdx.graphics.getHeight()/2);
        pauseOverlay.dispose();
        pauseTexture.dispose();

        // Отрисовка текста меню паузы
        font.draw(batch, "PAUSE", centerX - 30, centerY + 50);
        font.draw(batch, "ESC - Continue", centerX - 60, centerY);
        font.draw(batch, "ENTER - Return to main menu", centerX - 100, centerY - 30);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            dispose();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    private void renderUI() {
        float uiX = camera.position.x - camera.viewportWidth/2 + 10;
        float uiY = camera.position.y + camera.viewportHeight/2 - 10;

        // Отрисовка UI
        font.draw(batch, "Health: " + (int)player.getHealth() + "/" + (int)player.getMaxHealth(),
                 uiX, uiY);
        font.draw(batch, "Stamina: " + (int)player.getStamina() + "/" + (int)player.getMaxStamina(),
                 uiX, uiY - 20);
        font.draw(batch, "Coins: " + collectedCoins + "/" + totalCoins,
                 uiX, uiY - 40);
        font.draw(batch, "Ore: " + player.getInventory().size + " (need 2 for chest)",
                 uiX, uiY - 60);
        font.draw(batch, "Level: " + levelManager.getCurrentLevel() + "/" + levelManager.getTotalLevels(),
                 uiX, uiY - 80);

        if (player.isInventoryOpen()) {
            float inventoryX = camera.position.x - 100;
            float inventoryY = camera.position.y + 100;

            font.draw(batch, "Inventory:", inventoryX, inventoryY);
            font.draw(batch, "Ore: " + player.getInventory().size,
                     inventoryX + 10, inventoryY - 30);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        try {
            if (batch != null) batch.dispose();
            if (backgroundTexture != null) backgroundTexture.dispose();
            if (oreTexture != null) oreTexture.dispose();
            if (chestTexture != null) chestTexture.dispose();
            if (coinTexture != null) coinTexture.dispose();
            if (wallTexture != null) wallTexture.dispose();
            if (playerTexture != null) playerTexture.dispose();
            if (doorTexture != null) doorTexture.dispose();
            if (boxTexture != null) boxTexture.dispose();
            if (stackedBoxTexture != null) stackedBoxTexture.dispose();
            if (enemyTexture != null) enemyTexture.dispose();
            if (font != null) font.dispose();

            // Очищаем оба набора слоев
            disposeGameObjects(backgroundLayer);
            disposeGameObjects(objectLayer);
            disposeGameObjects(wallLayer);
            disposeGameObjects(nextBackgroundLayer);
            disposeGameObjects(nextObjectLayer);
            disposeGameObjects(nextWallLayer);
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error during dispose", e);
        }
    }

    private void disposeGameObjects(Array<GameObject> objects) {
        if (objects != null) {
            for (GameObject obj : objects) {
                if (obj != null) {
                    try {
                        obj.dispose();
                    } catch (Exception e) {
                        Gdx.app.error("GameScreen", "Error disposing game object", e);
                    }
                }
            }
            objects.clear();
        }
    }

    public void coinCollected() {
        collectedCoins++;

        // Проверяем все двери на уровне и открываем их, если собраны все монеты
        for (GameObject obj : objectLayer) {
            if (obj instanceof SecretDoor) {
                ((SecretDoor) obj).checkAndOpen(collectedCoins);
            }
        }

        // Проверяем условие победы на последнем уровне
        if (levelManager.getCurrentLevel() == levelManager.getTotalLevels() &&
            collectedCoins == totalCoins) {
            final VictoryScreen victoryScreen = new VictoryScreen(game);
            dispose();
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(victoryScreen);
                }
            });
        }
    }

    public void removeObject(GameObject obj) {
        objectLayer.removeValue(obj, true);
    }

    public int getCollectedCoins() {
        return collectedCoins;
    }

    /**
     * Обработка поражения игрока.
     * Создает и устанавливает экран поражения.
     */
    public void gameOver() {
        final GameOverScreen gameOverScreen = new GameOverScreen(game);
        dispose();
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                game.setScreen(gameOverScreen);
            }
        });
    }

    public Array<GameObject> getWallLayer() {
        return wallLayer;
    }

}
