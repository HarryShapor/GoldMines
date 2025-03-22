package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;

/**
 * Класс, представляющий стену в игре.
 * Базовый неподвижный объект, формирующий структуру уровня.
 * Блокирует движение игрока и других объектов.
 */
public class Wall extends GameObject {
    /**
     * Конструктор стены.
     * Создает стену стандартного размера (32x32 пикселя).
     * Стены являются статическими объектами и используются для построения уровня.
     * @param x Позиция по X
     * @param y Позиция по Y
     * @param texture Текстура стены
     */
    public Wall(float x, float y, Texture texture) {
        super(x, y, 32, 32, texture);
    }
} 