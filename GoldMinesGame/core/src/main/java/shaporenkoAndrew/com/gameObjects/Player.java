package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import shaporenkoAndrew.com.screens.GameScreen;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Класс, представляющий игрока в игре.
 * Основной игровой персонаж, которым управляет пользователь.
 * Функционал:
 * - Передвижение с помощью клавиш WASD
 * - Спринт при удержании Shift
 * - Система здоровья и выносливости
 * - Инвентарь для хранения руды
 * - Сбор монет и взаимодействие с объектами
 * - Добыча руды
 */
public class Player extends GameObject {
    // Константы для настройки характеристик игрока
    private static final float BASE_SPEED = 200f;
    private static final float SPRINT_MULTIPLIER = 1.5f;
    private static final float STAMINA_SPRINT_COST = 30f;
    private static final float STAMINA_MINING_COST = 20f;
    private static final float STAMINA_REGEN_RATE = 15f;
    private static final float MINING_DISTANCE = 70f;
    private static final float MINING_TIME = 1.0f;

    // Основные характеристики
    private float currentSpeed;
    private int coins;
    private Array<Ore> inventory;
    private boolean isInventoryOpen;
    private float miningProgress;
    private float maxHealth = 100f;
    private float currentHealth;
    private float maxStamina = 100f;
    private float currentStamina;
    private boolean isStaminaRegenPaused;
    private float speedMultiplier = 1.0f;

    // Система добычи руды
    private float miningTimer = 0;
    private Ore targetOre = null;

    // Система коллизий и движения
    private Vector2 previousPosition;
    private Vector2 currentPosition;
    private boolean isDead = false;
    private boolean isFacingLeft = false;

    private GameScreen gameScreen;
    private Array<GameObject> objectsToRemove = new Array<>();

    /**
     * Конструктор игрока.
     * Инициализирует все базовые параметры и системы игрока.
     * @param x Начальная позиция по X
     * @param y Начальная позиция по Y
     * @param texture Текстура игрока
     * @param gameScreen Ссылка на игровой экран
     */
    public Player(float x, float y, Texture texture, GameScreen gameScreen) {
        super(x, y, 32, 32, texture);
        this.gameScreen = gameScreen;
        this.currentSpeed = BASE_SPEED;
        this.coins = 0;
        this.inventory = new Array<>();
        this.isInventoryOpen = false;
        this.miningProgress = 0;
        this.currentHealth = maxHealth;
        this.currentStamina = maxStamina;
        this.previousPosition = new Vector2(x, y);
        this.currentPosition = new Vector2(x, y);
    }

    /**
     * Обновление состояния игрока.
     * Обрабатывает:
     * - Движение игрока
     * - Управление выносливостью
     * - Добычу руды
     * - Коллизии
     * @param delta Время с последнего обновления
     */
    @Override
    public void update(float delta) {
        if (isDead) return;

        previousPosition.set(x, y);

        // Обработка движения
        float moveX = 0;
        float moveY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveY += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveY -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX -= 1;
            isFacingLeft = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX += 1;
            isFacingLeft = true;
        }

        // Обработка спринта и выносливости
        boolean isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && currentStamina > 0;
        float currentBaseSpeed = BASE_SPEED * speedMultiplier;
        currentSpeed = isSprinting ? currentBaseSpeed * SPRINT_MULTIPLIER : currentBaseSpeed;

        if (isSprinting && (moveX != 0 || moveY != 0)) {
            currentStamina = Math.max(0, currentStamina - STAMINA_SPRINT_COST * delta);
            isStaminaRegenPaused = true;
        }

        // Обработка добычи руды
        if (targetOre != null) {
            miningTimer += delta;
            if (miningTimer >= MINING_TIME) {
                inventory.add(targetOre);
                gameScreen.removeObject(targetOre);
                targetOre = null;
                miningTimer = 0;
            }
        }

        // Восстановление выносливости
        if (!isSprinting) {
            if (isStaminaRegenPaused) {
                isStaminaRegenPaused = false;
            } else {
                currentStamina = Math.min(maxStamina, currentStamina + STAMINA_REGEN_RATE * delta);
            }
        }

        // Нормализация диагонального движения
        if (moveX != 0 && moveY != 0) {
            moveX *= 0.7071f;
            moveY *= 0.7071f;
        }

        // Применение движения с проверкой коллизий
        float newX = x + moveX * currentSpeed * delta;
        float newY = y + moveY * currentSpeed * delta;

        // Проверка коллизий по X
        x = newX;
        bounds.setPosition(x, y);
        boolean collisionX = false;
        for (GameObject wall : gameScreen.getWallLayer()) {
            if (bounds.overlaps(wall.getBounds())) {
                collisionX = true;
                break;
            }
        }
        if (collisionX) {
            x = previousPosition.x;
            bounds.setPosition(x, y);
        }

        // Проверка коллизий по Y
        y = newY;
        bounds.setPosition(x, y);
        boolean collisionY = false;
        for (GameObject wall : gameScreen.getWallLayer()) {
            if (bounds.overlaps(wall.getBounds())) {
                collisionY = true;
                break;
            }
        }
        if (collisionY) {
            y = previousPosition.y;
            bounds.setPosition(x, y);
        }

        currentPosition.set(x, y);
        bounds.setPosition(x, y);

        // Проверка открытия инвентаря
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            isInventoryOpen = !isInventoryOpen;
        }
    }

    /**
     * Отрисовка игрока и связанных элементов интерфейса.
     * @param batch SpriteBatch для отрисовки
     */
    @Override
    public void render(SpriteBatch batch) {
        // Отрисовка игрока с учетом направления
        if (isFacingLeft) {
            batch.draw(texture, x + bounds.width, y, -bounds.width, bounds.height);
        } else {
            batch.draw(texture, x, y, bounds.width, bounds.height);
        }

        // Отрисовка прогресса добычи руды
        if (targetOre != null) {
            renderMiningProgress(batch);
        }

        if (isInventoryOpen) {
            renderInventory(batch);
        }
    }

    /**
     * Отрисовка прогресса добычи руды.
     * @param batch SpriteBatch для отрисовки
     */
    private void renderMiningProgress(SpriteBatch batch) {
        float progress = getMiningProgress();
        float barWidth = 32;
        float barHeight = 4;
        float barX = targetOre.getX();
        float barY = targetOre.getY() + 36;

        // Фон полоски прогресса
        Pixmap bgPixmap = new Pixmap((int)barWidth, (int)barHeight, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.5f);
        bgPixmap.fillRectangle(0, 0, (int)barWidth, (int)barHeight);
        Texture bgTexture = new Texture(bgPixmap);
        batch.draw(bgTexture, barX, barY);
        bgPixmap.dispose();
        bgTexture.dispose();

        // Заполненная часть полоски прогресса
        Pixmap fgPixmap = new Pixmap((int)(barWidth * progress), (int)barHeight, Pixmap.Format.RGBA8888);
        fgPixmap.setColor(1, 1, 0, 1);
        fgPixmap.fillRectangle(0, 0, (int)(barWidth * progress), (int)barHeight);
        Texture fgTexture = new Texture(fgPixmap);
        batch.draw(fgTexture, barX, barY);
        fgPixmap.dispose();
        fgTexture.dispose();
    }

    /**
     * Отрисовка инвентаря игрока.
     * @param batch SpriteBatch для отрисовки
     */
    private void renderInventory(SpriteBatch batch) {
        // TODO: Реализовать отрисовку инвентаря
    }

    /**
     * Проверка коллизий с игровыми объектами.
     * @param objects Массив объектов для проверки
     */
    public void checkCollisions(Array<GameObject> objects) {
        objectsToRemove.clear();
        Array<GameObject> tempObjects = new Array<>(objects);
        for (GameObject obj : tempObjects) {
            if (bounds.overlaps(obj.getBounds())) {
                handleCollision(obj);
            }
        }
        objects.removeAll(objectsToRemove, true);
    }

    /**
     * Обработка коллизий с конкретным объектом.
     * @param obj Объект, с которым произошла коллизия
     */
    public void handleCollision(GameObject obj) {
        if (obj instanceof Coin) {
            coins++;
            objectsToRemove.add(obj);
            gameScreen.coinCollected();
        } else if (obj instanceof Chest && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Chest chest = (Chest) obj;
            if (!chest.isOpened()) {
                chest.open(this);
            }
        } else if (obj instanceof SecretDoor && Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            SecretDoor door = (SecretDoor) obj;
            door.setGameScreen(gameScreen);
            door.checkAndOpen(gameScreen.getCollectedCoins());
            if (door.isOpen()) {
                door.interact();
            }
        } else if (obj instanceof Wall || obj instanceof Box) {
            resolveCollision(obj.getBounds());
        }
    }

    /**
     * Разрешение коллизий с препятствиями.
     * @param otherBounds Границы объекта-препятствия
     */
    private void resolveCollision(Rectangle otherBounds) {
        if (bounds.overlaps(otherBounds)) {
            float overlapX = 0;
            float overlapY = 0;

            if (x < otherBounds.x) {
                overlapX = (x + bounds.width) - otherBounds.x;
            } else {
                overlapX = x - (otherBounds.x + otherBounds.width);
            }

            if (y < otherBounds.y) {
                overlapY = (y + bounds.height) - otherBounds.y;
            } else {
                overlapY = y - (otherBounds.y + otherBounds.height);
            }

            if (Math.abs(overlapX) < Math.abs(overlapY)) {
                x = previousPosition.x;
            } else {
                y = previousPosition.y;
            }

            bounds.setPosition(x, y);
            currentPosition.set(x, y);
        }
    }

    // Геттеры и сеттеры
    public int getCoins() { return coins; }
    public Array<Ore> getInventory() { return inventory; }
    public boolean isInventoryOpen() { return isInventoryOpen; }
    public float getMiningProgress() { return targetOre != null ? miningTimer / MINING_TIME : 0; }
    public float getHealth() { return currentHealth; }
    public float getMaxHealth() { return maxHealth; }
    public float getStamina() { return currentStamina; }
    public float getMaxStamina() { return maxStamina; }
    public boolean isDead() { return isDead; }

    /**
     * Нанесение урона игроку.
     * @param amount Количество урона
     */
    public void damage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    /**
     * Лечение игрока.
     * @param amount Количество восстанавливаемого здоровья
     */
    public void heal(float amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    /**
     * Установка значения выносливости.
     * @param stamina Новое значение выносливости
     */
    public void setStamina(float stamina) {
        this.currentStamina = Math.min(maxStamina, stamina);
    }

    public void setInventory(Array<Ore> inventory) { this.inventory = inventory; }
    public void setCoins(int coins) { this.coins = coins; }
    public void setDead(boolean dead) { isDead = dead; }

    /**
     * Увеличение максимального здоровья.
     * @param amount Величина увеличения
     */
    public void increaseMaxHealth(float amount) {
        maxHealth += amount;
        currentHealth += amount;
    }

    /**
     * Увеличение максимальной выносливости.
     * @param amount Величина увеличения
     */
    public void increaseMaxStamina(float amount) {
        maxStamina += amount;
        currentStamina += amount;
    }

    /**
     * Увеличение скорости передвижения.
     * @param percentage Процент увеличения скорости
     */
    public void increaseSpeed(float percentage) {
        speedMultiplier += percentage;
    }

    /**
     * Добавление монет игроку.
     * @param amount Количество добавляемых монет
     */
    public void addCoins(int amount) {
        this.coins += amount;
    }

    /**
     * Проверка наличия достаточного количества руды.
     * @return true если у игрока достаточно руды, false в противном случае
     */
    public boolean hasEnoughOre() {
        return inventory.size >= 2;
    }

    /**
     * Удаление руды из инвентаря.
     * @param amount Количество удаляемой руды
     */
    public void removeOre(int amount) {
        for (int i = 0; i < amount && inventory.size > 0; i++) {
            inventory.removeIndex(inventory.size - 1);
        }
    }

    /**
     * Установка цели для добычи руды.
     * @param ore Руда для добычи
     */
    public void setTargetOre(Ore ore) {
        float distance = Vector2.dst(x, y, ore.getX(), ore.getY());
        if (distance <= MINING_DISTANCE) {
            if (targetOre != ore) {
                targetOre = ore;
                miningTimer = 0;
            }
        } else {
            targetOre = null;
            miningTimer = 0;
        }
    }

    /**
     * Очистка цели добычи руды.
     */
    public void clearTargetOre() {
        targetOre = null;
        miningTimer = 0;
    }
}
