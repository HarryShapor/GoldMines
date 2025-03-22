package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import java.util.Random;

/**
 * Класс, представляющий сундук с сокровищами.
 * Функционал:
 * - Хранение ценных предметов
 * - Взаимодействие с игроком при открытии
 * - Анимация открытия/закрытия
 * - Выдача наград игроку
 */
public class Chest extends GameObject {
    private boolean isOpened = false;
    private int coins;
    private Random random = new Random();
    private Texture openTexture;

    /**
     * Конструктор сундука.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Текстура закрытого сундука
     * @param coins Количество монет в сундуке
     */
    public Chest(float x, float y, Texture texture, int coins) {
        super(x, y, 32, 32, texture);
        this.coins = coins;
        this.openTexture = new Texture(Gdx.files.internal("chest_golden_open_empty.png"));
    }

    /**
     * Открытие сундука игроком.
     * При открытии:
     * - Проверяется наличие необходимого количества руды у игрока
     * - Выдаются монеты
     * - Случайным образом выдается один из бонусов:
     *   * Восстановление здоровья (25% шанс)
     *   * Увеличение максимального здоровья
     *   * Увеличение максимальной выносливости
     *   * Увеличение скорости
     * @param player Игрок, открывающий сундук
     */
    public void open(Player player) {
        if (!isOpened && player.hasEnoughOre()) {
            isOpened = true;
            player.removeOre(2);
            player.addCoins(coins);

            // Меняем текстуру на открытый сундук
            texture = openTexture;

            // Шанс 25% на восстановление здоровья
            if (random.nextFloat() < 0.25f) {
                player.heal(40);
                return;
            }

            // Случайный бонус из остальных
            int bonusType = random.nextInt(3);
            switch (bonusType) {
                case 0:
                    player.increaseMaxHealth(20);
                    break;
                case 1:
                    player.increaseMaxStamina(20);
                    break;
                case 2:
                    player.increaseSpeed(0.2f);
                    break;
            }
        }
    }

    /**
     * Проверка состояния сундука.
     * @return true если сундук уже открыт, false если закрыт
     */
    public boolean isOpened() {
        return isOpened;
    }

    /**
     * Освобождение ресурсов сундука.
     * Освобождает память от текстур закрытого и открытого сундука.
     */
    @Override
    public void dispose() {
        super.dispose();
        if (openTexture != null) {
            openTexture.dispose();
        }
    }
}
