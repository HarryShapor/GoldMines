# Этап сборки
FROM gradle:7.6.1-jdk17 AS builder

WORKDIR /app
# Копируем все содержимое текущей директории
COPY . .
RUN ls -la
RUN pwd

# Этап запуска
FROM openjdk:17-slim

# Устанавливаем только минимально необходимые зависимости
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Копируем JAR файл
COPY GoldMinesGame.jar ./game.jar

# Переменные окружения для графики
ENV DISPLAY=:0
ENV LIBGL_ALWAYS_INDIRECT=1

# Запускаем приложение
CMD ["java", "-jar", "game.jar"] 