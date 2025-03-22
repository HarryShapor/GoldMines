package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;

/**
 * Класс, представляющий рудные залежи в игре.
 * Ценный ресурс, который можно добывать.
 * При сборе дает больше очков, чем обычные монеты.
 */
public class Ore extends GameObject {
    private int value; // ценность руды

    /**
     * Конструктор рудного месторождения.
     * Создает руду с заданными координатами и ценностью.
     * Руда имеет стандартный размер 32x32 пикселя.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Текстура руды
     * @param value Ценность руды (количество очков)
     */
    public Ore(float x, float y, Texture texture, int value) {
        super(x, y, 32, 32, texture);
        this.value = value;
    }
}
