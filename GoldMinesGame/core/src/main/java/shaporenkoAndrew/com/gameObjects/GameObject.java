package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Базовый абстрактный класс для всех игровых объектов.
 * Содержит основные свойства и методы, общие для всех объектов в игре:
 * - Позиция (x, y)
 * - Размеры (width, height)
 * - Текстура объекта
 * Используется как родительский класс для всех игровых сущностей
 */
public abstract class GameObject {
    protected float x, y;
    protected float width, height;
    protected Texture texture;
    protected Rectangle bounds;

    /**
     * Конструктор базового игрового объекта.
     * @param x Начальная позиция по X
     * @param y Начальная позиция по Y
     * @param width Ширина объекта
     * @param height Высота объекта
     * @param texture Текстура для отрисовки объекта
     */
    public GameObject(float x, float y, float width, float height, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Обновление состояния объекта.
     * Вызывается каждый кадр для обновления позиции и других параметров.
     * @param delta Время, прошедшее с последнего обновления
     */
    public void update(float delta) {
        bounds.setPosition(x, y);
    }

    /**
     * Отрисовка объекта на экране.
     * @param batch SpriteBatch для отрисовки текстур
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    /**
     * Освобождение ресурсов объекта.
     * Вызывается при удалении объекта для очистки памяти.
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    /**
     * Получение границ объекта для обработки коллизий.
     * @return Rectangle с текущими границами объекта
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Получение текущей X-координаты объекта.
     * @return Позиция по X
     */
    public float getX() { return x; }

    /**
     * Получение текущей Y-координаты объекта.
     * @return Позиция по Y
     */
    public float getY() { return y; }
}
