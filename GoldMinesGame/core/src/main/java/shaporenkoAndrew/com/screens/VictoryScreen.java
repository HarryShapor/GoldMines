package shaporenkoAndrew.com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import shaporenkoAndrew.com.Main;

/**
 * Экран, отображаемый при победе в игре.
 * Функционал:
 * - Поздравление игрока с победой
 * - Отображение финального счета
 * - Кнопки для начала новой игры или возврата в меню
 */
public class VictoryScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final Texture victoryTexture;
    private final BitmapFont font;

    /**
     * Конструктор экрана победы.
     * Инициализирует компоненты для отрисовки поздравления и текста.
     * @param game Экземпляр основного класса игры
     */
    public VictoryScreen(final Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.victoryTexture = new Texture(Gdx.files.internal("you_win.jpg"));
        this.font = new BitmapFont();
        this.font.getData().setScale(2);
    }

    /**
     * Метод отрисовки экрана.
     * Отображает поздравление с победой и инструкции для игрока.
     * Обрабатывает нажатия клавиш для перезапуска или выхода.
     * @param delta Время, прошедшее с последнего кадра
     */
    @Override
    public void render(float delta) {
        // Обработка клавиш
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            dispose();
            game.setScreen(new GameScreen(game));
            return;
        } else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            dispose();
            Gdx.app.exit();
            return;
        }

        // Очистка экрана
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        // Отрисовка изображения победы
        float x = (Gdx.graphics.getWidth() - victoryTexture.getWidth()) / 2f;
        float y = (Gdx.graphics.getHeight() - victoryTexture.getHeight()) / 2f;
        batch.draw(victoryTexture, x, y);

        // Отрисовка текста
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = y - 50;
        font.draw(batch, "Press ENTER to restart game", centerX - 150, centerY);
        font.draw(batch, "Press ESC to exit", centerX - 100, centerY - 40);
        batch.end();
    }

    /**
     * Обработка изменения размера окна.
     * @param width Новая ширина окна
     * @param height Новая высота окна
     */
    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    /**
     * Освобождение ресурсов экрана.
     * Освобождает память от текстур, шрифтов и других ресурсов.
     */
    @Override
    public void dispose() {
        batch.dispose();
        victoryTexture.dispose();
        font.dispose();
    }
} 