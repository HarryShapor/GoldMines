package shaporenkoAndrew.com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import shaporenkoAndrew.com.Main;

/**
 * Экран, отображаемый при проигрыше.
 * Функционал:
 * - Отображение сообщения о проигрыше
 * - Показ набранных очков
 * - Кнопки для перезапуска или возврата в меню
 */
public class GameOverScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private final Texture gameOverTexture;

    /**
     * Конструктор экрана проигрыша.
     * Инициализирует компоненты для отрисовки текста и сообщений.
     * @param game Экземпляр основного класса игры
     */
    public GameOverScreen(final Main game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
        this.gameOverTexture = new Texture(Gdx.files.internal("game_over.jpg"));
    }

    /**
     * Метод отрисовки экрана.
     * Отображает сообщение о проигрыше и инструкции для игрока.
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
        // Отрисовка изображения поражения
        float x = (Gdx.graphics.getWidth() - gameOverTexture.getWidth()) / 2f;
        float y = (Gdx.graphics.getHeight() - gameOverTexture.getHeight()) / 2f;
        batch.draw(gameOverTexture, x, y);

        // Отрисовка текста
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = y - 50;
        font.draw(batch, "Press ENTER to restart game", centerX - 150, centerY);
        font.draw(batch, "Press ESC to exit", centerX - 100, centerY - 40);
        batch.end();
    }

    /**
     * Освобождение ресурсов экрана.
     * Освобождает память от шрифтов и других ресурсов.
     */
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        gameOverTexture.dispose();
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
} 