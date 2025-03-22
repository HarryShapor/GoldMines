package shaporenkoAndrew.com.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import shaporenkoAndrew.com.gameObjects.*;
import com.badlogic.gdx.graphics.Texture;
import shaporenkoAndrew.com.screens.GameScreen;

/**
 * Класс, отвечающий за процедурную генерацию уровней игры.
 * Функционал:
 * - Создание случайных лабиринтов
 * - Размещение игровых объектов (монеты, сундуки, враги)
 * - Генерация безопасных путей для прохождения
 * - Создание секретных комнат и проходов
 */
public class LevelGenerator {
    // Константы размеров и параметров генерации
    private static final int TILE_SIZE = 32;
    private final int levelWidth;
    private final int levelHeight;
    private final int[][] levelData; // 0 - пустота, 1 - стена, 2 - руда, 3 - сундук, 4 - монета, 5 - потайная дверь
    private Array<Rectangle> rooms;
    private final int minRoomSize;
    private final int maxRoomSize;
    private final int maxRooms;
    private final int corridorWidth;
    private final int maxCoins;
    private int totalCoins = 0;
    private int requiredCoins = 0;
    private Rectangle secretRoom;
    private float enemySpawnRate = 1.0f;

    /**
     * Конструктор генератора уровней.
     * Инициализирует все параметры генерации и запускает процесс создания уровня.
     * @param width Ширина уровня в пикселях
     * @param height Высота уровня в пикселях
     * @param minRooms Минимальное количество комнат
     * @param maxRooms Максимальное количество комнат
     * @param minRoomSize Минимальный размер комнаты
     * @param maxRoomSize Максимальный размер комнаты
     * @param corridorWidth Ширина коридоров
     * @param maxCoins Максимальное количество монет на уровне
     */
    public LevelGenerator(int width, int height, int minRooms, int maxRooms,
                         int minRoomSize, int maxRoomSize, int corridorWidth, int maxCoins) {
        this.levelWidth = width / TILE_SIZE;
        this.levelHeight = height / TILE_SIZE;
        this.levelData = new int[levelWidth][levelHeight];
        this.rooms = new Array<>();
        this.minRoomSize = minRoomSize;
        this.maxRoomSize = maxRoomSize;
        this.maxRooms = maxRooms;
        this.corridorWidth = corridorWidth;
        this.maxCoins = maxCoins;
        generateLevel();
    }

    /**
     * Основной метод генерации уровня.
     * Последовательно выполняет все этапы создания уровня:
     * 1. Заполнение уровня стенами
     * 2. Генерация комнат
     * 3. Соединение комнат коридорами
     * 4. Размещение объектов
     */
    private void generateLevel() {
        // Заполняем всё стенами
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                levelData[x][y] = 1;
            }
        }
        generateRooms();
        connectRooms();
        populateRooms();
    }

    /**
     * Генерация комнат на уровне.
     * Создает случайные прямоугольные комнаты, проверяя их на пересечение.
     * Одна из комнат (кроме первой) может быть выбрана как секретная.
     */
    private void generateRooms() {
        int attempts = 0;
        while (rooms.size < maxRooms && attempts < 100) {
            int roomWidth = MathUtils.random(minRoomSize, maxRoomSize);
            int roomHeight = MathUtils.random(minRoomSize, maxRoomSize);
            int x = MathUtils.random(1, levelWidth - roomWidth - 1);
            int y = MathUtils.random(1, levelHeight - roomHeight - 1);

            Rectangle newRoom = new Rectangle(x, y, roomWidth, roomHeight);
            boolean overlaps = false;

            for (Rectangle room : rooms) {
                if (room.overlaps(newRoom)) {
                    overlaps = true;
                    break;
                }
            }

            if (!overlaps) {
                carveRoom(newRoom);
                rooms.add(newRoom);

                if (rooms.size > 1 && (secretRoom == null || MathUtils.random() < 0.2f)) {
                    secretRoom = newRoom;
                }
            }
            attempts++;
        }
    }

    /**
     * Вырезание комнаты в массиве уровня.
     * Заменяет стены (1) на пустое пространство (0) в области комнаты.
     * @param room Прямоугольник, определяющий размеры и положение комнаты
     */
    private void carveRoom(Rectangle room) {
        for (int x = (int)room.x; x < room.x + room.width; x++) {
            for (int y = (int)room.y; y < room.y + room.height; y++) {
                levelData[x][y] = 0;
            }
        }
    }

    /**
     * Соединение комнат коридорами.
     * Создает L-образные коридоры между последовательными комнатами.
     * Случайным образом выбирает, какая часть коридора будет горизонтальной, а какая вертикальной.
     */
    private void connectRooms() {
        for (int i = 0; i < rooms.size - 1; i++) {
            Rectangle roomA = rooms.get(i);
            Rectangle roomB = rooms.get(i + 1);

            int x1 = (int)(roomA.x + roomA.width / 2);
            int y1 = (int)(roomA.y + roomA.height / 2);
            int x2 = (int)(roomB.x + roomB.width / 2);
            int y2 = (int)(roomB.y + roomB.height / 2);

            if (MathUtils.randomBoolean()) {
                carveHorizontalCorridor(x1, x2, y1);
                carveVerticalCorridor(y1, y2, x2);
            } else {
                carveVerticalCorridor(y1, y2, x1);
                carveHorizontalCorridor(x1, x2, y2);
            }
        }
    }

    /**
     * Создание горизонтального коридора.
     * @param x1 Начальная X-координата
     * @param x2 Конечная X-координата
     * @param y Y-координата коридора
     */
    private void carveHorizontalCorridor(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int offset = -(corridorWidth/2); offset <= corridorWidth/2; offset++) {
                int newY = y + offset;
                if (newY >= 0 && newY < levelHeight) {
                    levelData[x][newY] = 0;
                }
            }
        }
    }

    /**
     * Создание вертикального коридора.
     * @param y1 Начальная Y-координата
     * @param y2 Конечная Y-координата
     * @param x X-координата коридора
     */
    private void carveVerticalCorridor(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            for (int offset = -(corridorWidth/2); offset <= corridorWidth/2; offset++) {
                int newX = x + offset;
                if (newX >= 0 && newX < levelWidth) {
                    levelData[newX][y] = 0;
                }
            }
        }
    }

    /**
     * Размещение игровых объектов в комнатах.
     * Последовательно размещает:
     * 1. Сундуки (ровно 3)
     * 2. Рудные залежи (5-10 штук)
     * 3. Монеты (до maxCoins)
     * 4. Секретную дверь в выбранной комнате
     */
    private void populateRooms() {
        totalCoins = 0;
        int totalOres = 0;
        int totalChests = 0;

        // Размещение сундуков
        Array<Rectangle> availableRooms = new Array<>(rooms);
        while (totalChests < 3 && availableRooms.size > 0) {
            placeChest(availableRooms, totalChests);
            totalChests++;
        }

        // Размещение руды
        int maxOres = MathUtils.random(5, 10);
        for (Rectangle room : rooms) {
            if (totalOres >= maxOres) break;
            placeOre(room, maxOres, totalOres);
            totalOres += Math.min(2, maxOres - totalOres);
        }

        // Размещение монет
        for (Rectangle room : rooms) {
            if (totalCoins >= maxCoins) break;
            placeCoins(room);
        }

        // Размещение секретной двери
        if (secretRoom != null) {
            int doorX = (int)(secretRoom.x + secretRoom.width - 3);
            int doorY = (int)(secretRoom.y + secretRoom.height - 3);
            levelData[doorX][doorY] = 5;
        }

        requiredCoins = totalCoins;
    }

    /**
     * Размещение сундука в случайной комнате.
     * @param availableRooms Список доступных комнат
     * @param chestCount Текущее количество размещенных сундуков
     */
    private void placeChest(Array<Rectangle> availableRooms, int chestCount) {
        int roomIndex = MathUtils.random(availableRooms.size - 1);
        Rectangle room = availableRooms.get(roomIndex);
        availableRooms.removeIndex(roomIndex);

        int x, y;
        do {
            x = (int)MathUtils.random(room.x + 1, room.x + room.width - 2);
            y = (int)MathUtils.random(room.y + 1, room.y + room.height - 2);
        } while (levelData[x][y] != 0);

        levelData[x][y] = 3;
    }

    /**
     * Размещение руды в комнате.
     * @param room Комната для размещения
     * @param maxOres Максимальное количество руды
     * @param totalOres Текущее количество размещенной руды
     */
    private void placeOre(Rectangle room, int maxOres, int totalOres) {
        int centerX = (int)(room.x + room.width / 2);
        int centerY = (int)(room.y + room.height / 2);
        int oreCount = Math.min(2, maxOres - totalOres);

        for (int i = 0; i < oreCount; i++) {
            int x, y;
            do {
                x = (int)MathUtils.random(room.x + 1, room.x + room.width - 2);
                y = (int)MathUtils.random(room.y + 1, room.y + room.height - 2);
            } while (Math.abs(x - centerX) < 3 && Math.abs(y - centerY) < 3 && levelData[x][y] != 0);

            if (levelData[x][y] == 0) {
                levelData[x][y] = 2;
            }
        }
    }

    /**
     * Размещение монет в комнате.
     * @param room Комната для размещения монет
     */
    private void placeCoins(Rectangle room) {
        int remainingCoins = maxCoins - totalCoins;
        int coinsPerRoom = Math.min(remainingCoins, MathUtils.random(3, 7));

        for (int i = 0; i < coinsPerRoom; i++) {
            int x, y;
            do {
                x = (int)MathUtils.random(room.x + 1, room.x + room.width - 2);
                y = (int)MathUtils.random(room.y + 1, room.y + room.height - 2);
            } while (levelData[x][y] != 0);

            if (levelData[x][y] == 0) {
                levelData[x][y] = 4;
                totalCoins++;
            }
        }
    }

    /**
     * Создание игровых объектов на основе сгенерированных данных.
     * Преобразует числовые данные уровня в реальные игровые объекты.
     * @param backgroundLayer Слой фона
     * @param objectLayer Слой игровых объектов
     * @param wallLayer Слой стен
     * @param backgroundTexture Текстура фона
     * @param wallTexture Текстура стен
     * @param oreTexture Текстура руды
     * @param chestTexture Текстура сундука
     * @param coinTexture Текстура монеты
     * @param doorTexture Текстура двери
     * @param boxTexture Текстура ящика
     * @param stackedBoxTexture Текстура составного ящика
     * @param skipDoor Флаг пропуска создания двери
     */
    public void createGameObjects(
            Array<GameObject> backgroundLayer,
            Array<GameObject> objectLayer,
            Array<GameObject> wallLayer,
            Texture backgroundTexture,
            Texture wallTexture,
            Texture oreTexture,
            Texture chestTexture,
            Texture coinTexture,
            Texture doorTexture,
            Texture boxTexture,
            Texture stackedBoxTexture,
            boolean skipDoor) {

        float tileSize = 32;

        // Создание объектов на основе levelData
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                float worldX = x * tileSize;
                float worldY = y * tileSize;

                backgroundLayer.add(new GameObject(worldX, worldY, tileSize, tileSize, backgroundTexture) {});

                switch (levelData[x][y]) {
                    case 1: wallLayer.add(new Wall(worldX, worldY, wallTexture)); break;
                    case 2: objectLayer.add(new Ore(worldX, worldY, oreTexture, MathUtils.random(5, 15))); break;
                    case 3: objectLayer.add(new Chest(worldX, worldY, chestTexture, MathUtils.random(10, 50))); break;
                    case 4: objectLayer.add(new Coin(worldX, worldY, coinTexture)); break;
                    case 5: if (!skipDoor) {
                        objectLayer.add(new SecretDoor(worldX, worldY, doorTexture, requiredCoins));
                    } break;
                }
            }
        }

        // Размещение ящиков
        placeBoxes(objectLayer, boxTexture, stackedBoxTexture);
    }

    /**
     * Размещение ящиков в комнатах и коридорах.
     * @param objectLayer Слой игровых объектов
     * @param boxTexture Текстура обычного ящика
     * @param stackedBoxTexture Текстура составного ящика
     */
    private void placeBoxes(Array<GameObject> objectLayer, Texture boxTexture, Texture stackedBoxTexture) {
        // Размещение в комнатах
        for (Rectangle room : rooms) {
            int boxCount = MathUtils.random(1, 3);
            for (int i = 0; i < boxCount; i++) {
                tryPlaceBox(room, objectLayer, boxTexture, stackedBoxTexture);
            }
        }

        // Размещение в коридорах
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                if (levelData[x][y] == 0 && levelData[x][y-1] == 1 && MathUtils.randomBoolean(0.1f)) {
                    boolean isStacked = MathUtils.randomBoolean(0.3f);
                    objectLayer.add(new Box(x * TILE_SIZE, y * TILE_SIZE,
                                          isStacked ? stackedBoxTexture : boxTexture,
                                          isStacked));
                }
            }
        }
    }

    /**
     * Попытка размещения ящика в комнате.
     * @param room Комната для размещения
     * @param objectLayer Слой игровых объектов
     * @param boxTexture Текстура обычного ящика
     * @param stackedBoxTexture Текстура составного ящика
     */
    private void tryPlaceBox(Rectangle room, Array<GameObject> objectLayer, 
                           Texture boxTexture, Texture stackedBoxTexture) {
        int attempts = 0;
        while (attempts < 10) {
            int x = (int)MathUtils.random(room.x + 1, room.x + room.width - 2);
            int y = (int)MathUtils.random(room.y + 1, room.y + room.height - 2);

            if (levelData[x][y] == 0 && levelData[x][y+1] == 0 && levelData[x][y-1] == 1) {
                boolean isStacked = MathUtils.randomBoolean(0.3f);
                objectLayer.add(new Box(x * TILE_SIZE, y * TILE_SIZE,
                                      isStacked ? stackedBoxTexture : boxTexture,
                                      isStacked));
                break;
            }
            attempts++;
        }
    }

    /**
     * Создание врагов на уровне.
     * @param objectLayer Слой игровых объектов
     * @param wallLayer Слой стен
     * @param enemyTexture Текстура врага
     * @param player Ссылка на игрока
     * @param gameScreen Ссылка на игровой экран
     */
    public void createEnemies(
            Array<GameObject> objectLayer,
            Array<GameObject> wallLayer,
            Texture enemyTexture,
            Player player,
            GameScreen gameScreen) {
        int maxEnemies = (int)(5 * enemySpawnRate);
        int enemyCount = 0;
        Array<Rectangle> availableRooms = new Array<>(rooms);
        float MIN_DISTANCE_FROM_PLAYER = 1000f;

        while (enemyCount < maxEnemies && availableRooms.size > 0) {
            trySpawnEnemy(availableRooms, objectLayer, wallLayer, enemyTexture, 
                         player, gameScreen, MIN_DISTANCE_FROM_PLAYER);
            enemyCount++;
        }
    }

    /**
     * Попытка создания врага в случайной комнате.
     * @param availableRooms Список доступных комнат
     * @param objectLayer Слой игровых объектов
     * @param wallLayer Слой стен
     * @param enemyTexture Текстура врага
     * @param player Ссылка на игрока
     * @param gameScreen Ссылка на игровой экран
     * @param minDistance Минимальная дистанция от игрока
     */
    private void trySpawnEnemy(Array<Rectangle> availableRooms, Array<GameObject> objectLayer,
                             Array<GameObject> wallLayer, Texture enemyTexture,
                             Player player, GameScreen gameScreen, float minDistance) {
        int roomIndex = MathUtils.random(availableRooms.size - 1);
        Rectangle room = availableRooms.get(roomIndex);
        int attempts = 0;

        while (attempts < 20) {
            int x = (int)MathUtils.random(room.x + 1, room.x + room.width - 2);
            int y = (int)MathUtils.random(room.y + 1, room.y + room.height - 2);

            float worldX = x * TILE_SIZE;
            float worldY = y * TILE_SIZE;

            float distanceToPlayer = Vector2.dst(worldX, worldY, player.getX(), player.getY());

            if (levelData[x][y] == 0 && distanceToPlayer >= minDistance) {
                objectLayer.add(new Enemy(worldX, worldY, enemyTexture, player, wallLayer, gameScreen));
                break;
            }
            attempts++;
        }

        availableRooms.removeIndex(roomIndex);
    }

    /**
     * Получение случайной комнаты для размещения игрока.
     * @return Случайная комната из списка комнат
     */
    public Rectangle getRandomRoom() {
        return rooms.get(MathUtils.random(rooms.size - 1));
    }

    /**
     * Получение общего количества монет на уровне.
     * @return Количество монет
     */
    public int getTotalCoins() {
        return totalCoins;
    }

    /**
     * Установка коэффициента появления врагов.
     * @param rate Коэффициент появления (0.0 - 1.0)
     */
    public void setEnemySpawnRate(float rate) {
        this.enemySpawnRate = rate;
    }
}
