package shaporenkoAndrew.com.world;

/**
 * Класс для управления уровнями игры.
 * Отвечает за:
 * - Хранение информации о текущем уровне
 * - Подсчет очков и прогресса игрока
 * - Управление переходами между уровнями
 */
public class LevelManager {
    // Константы игры
    private static final int TOTAL_LEVELS = 2;
    private int currentLevel;
    
    // Параметры генерации уровней
    private static final int[] MIN_ROOMS = {8, 12, 15};      // Минимальное количество комнат для каждого уровня
    private static final int[] MAX_ROOMS = {12, 16, 20};     // Максимальное количество комнат
    private static final int[] MIN_ROOM_SIZE = {8, 10, 12};  // Минимальный размер комнаты
    private static final int[] MAX_ROOM_SIZE = {12, 16, 20}; // Максимальный размер комнаты
    private static final int[] CORRIDOR_WIDTH = {2, 3, 4};   // Ширина коридоров
    private static final int[] MAX_COINS = {30, 40, 50};     // Максимальное количество монет
    
    /**
     * Конструктор менеджера уровней.
     * Инициализирует игру с первого уровня.
     */
    public LevelManager() {
        this.currentLevel = 0;
    }

    /**
     * Создает новый генератор уровня с параметрами, соответствующими текущему уровню.
     * @param width Ширина уровня в пикселях
     * @param height Высота уровня в пикселях
     * @return Новый экземпляр LevelGenerator с настроенными параметрами
     */
    public LevelGenerator generateLevel(int width, int height) {
        return new LevelGenerator(
            width, 
            height,
            MIN_ROOMS[currentLevel],
            MAX_ROOMS[currentLevel],
            MIN_ROOM_SIZE[currentLevel],
            MAX_ROOM_SIZE[currentLevel],
            CORRIDOR_WIDTH[currentLevel],
            MAX_COINS[currentLevel]
        );
    }

    /**
     * Проверяет, есть ли следующий уровень.
     * @return true если есть следующий уровень, false если текущий уровень последний
     */
    public boolean hasNextLevel() {
        return currentLevel < TOTAL_LEVELS - 1;
    }

    /**
     * Переход к следующему уровню.
     * Увеличивает номер текущего уровня, если есть следующий уровень.
     */
    public void nextLevel() {
        if (hasNextLevel()) {
            currentLevel++;
        }
    }

    /**
     * Получение номера текущего уровня.
     * @return Номер текущего уровня (начиная с 1)
     */
    public int getCurrentLevel() {
        return currentLevel + 1;
    }

    /**
     * Получение общего количества уровней в игре.
     * @return Общее количество уровней
     */
    public int getTotalLevels() {
        return TOTAL_LEVELS;
    }
} 