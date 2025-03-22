package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import shaporenkoAndrew.com.screens.GameScreen;

/**
 * Класс, представляющий секретную дверь в игре.
 * Функционал:
 * - Скрытый проход в секретные области
 * - Открывается при определенных условиях
 * - Может содержать особые награды
 * - Анимация открытия/закрытия
 */
public class SecretDoor extends GameObject {
    private boolean isOpen = false;
    private GameScreen gameScreen;
    private int requiredCoins;
    private Texture closedTexture;
    private Texture openTexture;

    /**
     * Конструктор секретной двери.
     * Инициализирует дверь с закрытым состоянием и загружает необходимые текстуры.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Базовая текстура двери
     * @param requiredCoins Количество монет, необходимое для открытия двери
     */
    public SecretDoor(float x, float y, Texture texture, int requiredCoins) {
        super(x, y, 32, 32, texture);
        this.requiredCoins = requiredCoins;
        this.closedTexture = new Texture(Gdx.files.internal("door_closed.png"));
        this.openTexture = new Texture(Gdx.files.internal("door_open.png"));
        this.texture = closedTexture;
    }

    /**
     * Устанавливает ссылку на игровой экран для взаимодействия.
     * @param gameScreen Текущий игровой экран
     */
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    /**
     * Проверяет условия открытия двери и открывает её при выполнении условий.
     * @param collectedCoins Текущее количество собранных монет
     */
    public void checkAndOpen(int collectedCoins) {
        if (collectedCoins >= requiredCoins && !isOpen) {
            isOpen = true;
            texture = openTexture;
        }
    }

    /**
     * Обрабатывает взаимодействие с дверью.
     * Если дверь открыта, осуществляет переход на следующий уровень.
     */
    public void interact() {
        if (isOpen && gameScreen != null) {
            gameScreen.nextLevel();
        }
    }

    /**
     * Проверяет состояние двери.
     * @return true если дверь открыта, false если закрыта
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Освобождает ресурсы, занятые текстурами двери.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (closedTexture != null) {
            closedTexture.dispose();
        }
        if (openTexture != null) {
            openTexture.dispose();
        }
    }

    /**
     * Отрисовывает дверь с зеленым оттенком для лучшей видимости.
     * @param batch SpriteBatch для отрисовки
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }
} 