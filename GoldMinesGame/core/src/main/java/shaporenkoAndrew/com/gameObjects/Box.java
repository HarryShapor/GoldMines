package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;

/**
 * Класс, представляющий коробку в игре.
 * Простой неподвижный объект, с которым может взаимодействовать игрок.
 * Используется как препятствие и элемент окружения.
 */
public class Box extends GameObject {
    private final boolean isStacked;

    /**
     * Конструктор коробки.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Текстура коробки
     * @param isStacked Флаг, указывающий является ли коробка составной (двойной)
     */
    public Box(float x, float y, Texture texture, boolean isStacked) {
        super(x, y, 32, 32, texture);
        this.isStacked = isStacked;
    }

}
