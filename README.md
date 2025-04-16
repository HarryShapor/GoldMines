# 🎮 GoldMines Game

![Java](https://img.shields.io/badge/Java-17-red.svg)
![Gradle](https://img.shields.io/badge/Gradle-7.6.1-green.svg)
![LWJGL](https://img.shields.io/badge/LWJGL-3-blue.svg)

## 📝 Описание

GoldMines - это игра, разработанная на Java с использованием фреймворка LWJGL 3. Проект построен на Gradle и использует современные практики разработки игр.

## 🚀 Особенности

- Современный стек технологий (Java 17, LWJGL 3)
- Кроссплатформенность
- Оптимизированная производительность
- Удобная система сборки через Gradle

## 🛠 Технический стек

- **Java 17** - основной язык программирования
- **LWJGL 3** - фреймворк для разработки игр
- **Gradle** - система сборки
- **Docker** - контейнеризация

## 📋 Требования

- Java 17 или выше
- Gradle 7.6.1
- Docker (опционально)

## 🚀 Запуск

### Локальный запуск

1. Клонируйте репозиторий:
```bash
git clone [URL репозитория]
```

2. Перейдите в директорию проекта:
```bash
cd GoldMinesGame
```

3. Запустите проект через Gradle:
```bash
./gradlew lwjgl3:run
```

### Запуск через Docker

1. Соберите Docker образ:
```bash
docker build -t goldmines-game .
```

2. Запустите контейнер:
```bash
# Для Windows
docker run -it --rm -e DISPLAY=host.docker.internal:0 goldmines-game

# Для Linux
xhost +local:docker
docker run -it --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix goldmines-game
```

## 📁 Структура проекта

```
GoldMinesGame/
├── assets/          # Ресурсы игры
├── core/            # Основной код игры
├── lwjgl3/          # LWJGL3 специфичный код
├── gradle/          # Конфигурация Gradle
└── build.gradle     # Конфигурация сборки
```

## 👥 Авторы

- Ваше имя - [@ваш_github](https://github.com/harryshapor)
