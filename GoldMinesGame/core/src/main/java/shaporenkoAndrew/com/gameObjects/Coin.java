package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;

/**
 * Класс, представляющий монету в игре.
 * Собираемый предмет, который увеличивает счет игрока.
 * При подборе игроком исчезает и добавляет очки.
 */
public class Coin extends GameObject {
    private int value;

    /**
     * Конструктор монеты.
     * Создает монету с заданными координатами и стандартным значением в 1 очко.
     * Монеты имеют уменьшенный размер (16x16) по сравнению с другими объектами.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Текстура монеты
     */
    public Coin(float x, float y, Texture texture) {
        super(x, y, 16, 16, texture); // монеты меньше других объектов
        this.value = 1;
    }
}
