package shaporenkoAndrew.com;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import shaporenkoAndrew.com.screens.MainMenuScreen;

/**
 * Главный класс игры, который инициализирует основные параметры приложения.
 * Отвечает за:
 * - Настройку размера окна
 * - Установку заголовка игры
 * - Инициализацию первого экрана (главное меню)
 */
public class Main extends Game {
    private int screenWidth;
    private int screenHeight;

    /**
     * Метод инициализации игры, вызывается при запуске.
     * - Получает текущее разрешение экрана
     * - Устанавливает оконный режим с учетом размера экрана
     * - Задает заголовок окна
     * - Создает и устанавливает экран главного меню
     */
    @Override
    public void create() {
        // Получаем текущий режим дисплея
        DisplayMode displayMode = Gdx.graphics.getDisplayMode();
        screenWidth = displayMode.width;
        screenHeight = displayMode.height;
        
        // Устанавливаем оконный режим на весь экран
        Gdx.graphics.setWindowedMode(screenWidth, screenHeight-50);
        Gdx.graphics.setTitle("Gold Mines");
        
        // Устанавливаем экран главного меню
        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * Метод освобождения ресурсов.
     * Вызывается при закрытии игры для корректного освобождения памяти.
     */
    @Override
    public void dispose() {
        super.dispose();
    }
}
