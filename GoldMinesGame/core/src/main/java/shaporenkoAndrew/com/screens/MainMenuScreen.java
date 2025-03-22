package shaporenkoAndrew.com.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import shaporenkoAndrew.com.Main;

/**
 * Экран главного меню игры.
 * Функционал:
 * - Отображение кнопок для начала игры и выхода
 * - Обработка пользовательского ввода в меню
 * - Управление переходами между экранами
 * - Отображение названия игры и фона
 */
public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture startButtonTexture;
    private Texture optionsButtonTexture;
    private Texture exitButtonTexture;

    /**
     * Конструктор экрана главного меню.
     * Инициализирует все компоненты интерфейса и настраивает обработчики событий.
     * @param game Экземпляр основного класса игры
     */
    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();

        // Загрузка текстур
        backgroundTexture = new Texture(Gdx.files.internal("fon_main_menu.jpg"));
        startButtonTexture = new Texture(Gdx.files.internal("play.png"));
        optionsButtonTexture = new Texture(Gdx.files.internal("options.png"));
        exitButtonTexture = new Texture(Gdx.files.internal("exit.png"));

        // Создание и настройка интерфейса
        setupUI();
    }

    /**
     * Настройка пользовательского интерфейса.
     * Создает и размещает все элементы меню, настраивает обработчики нажатий.
     */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center().left().padLeft(50);

        Image startButton = new Image(startButtonTexture);
        Image optionsButton = new Image(optionsButtonTexture);
        Image exitButton = new Image(exitButtonTexture);

        // Настройка обработчиков нажатий
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Реализовать переход к настройкам
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dispose();
                Gdx.app.exit();
            }
        });

        table.add(startButton).pad(10).row();
        table.add(optionsButton).pad(10).row();
        table.add(exitButton).pad(10);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Метод отображения экрана.
     * Вызывается при первом показе экрана.
     */
    @Override
    public void show() {
    }

    /**
     * Метод отрисовки экрана.
     * Отрисовывает фон и все элементы интерфейса.
     * @param delta Время, прошедшее с последнего кадра
     */
    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    /**
     * Обработка изменения размера окна.
     * @param width Новая ширина окна
     * @param height Новая высота окна
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    /**
     * Освобождение ресурсов экрана.
     * Освобождает память от текстур и других ресурсов.
     */
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        backgroundTexture.dispose();
        startButtonTexture.dispose();
        optionsButtonTexture.dispose();
        exitButtonTexture.dispose();
    }
}
