package shaporenkoAndrew.com.gameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import shaporenkoAndrew.com.screens.GameScreen;

/**
 * Класс, представляющий врага в игре.
 * Реализует:
 * - Искусственный интеллект для преследования игрока
 * - Систему патрулирования территории
 * - Обработку столкновений с игроком
 * - Анимацию врага
 */
public class Enemy extends GameObject {
    // Константы поведения врага
    private static final float SPEED = 150f;
    private static final float PATROL_SPEED = 100f;
    private static final float VISION_RADIUS = 300f;
    private static final float OBSTACLE_AVOIDANCE_RADIUS = 50f;
    private static final float PATROL_TIME = 2f;
    private static final float DAMAGE_AMOUNT = 20f;
    private static final float DAMAGE_COOLDOWN = 2.0f;
    private static final float ATTACK_RANGE = 50f;

    // Основные компоненты
    private Player player;
    private Vector2 velocity;
    private Vector2 desiredDirection;
    private Vector2 avoidanceForce;
    private Array<GameObject> walls;
    private GameScreen gameScreen;
    
    // Система патрулирования
    private float patrolTimer;
    private Vector2 patrolDirection;
    private boolean isChasing;
    private float attackTimer = DAMAGE_COOLDOWN;

    // Система предотвращения застревания
    private float stuckTimer;
    private Vector2 lastPosition;
    private boolean isStuck;
    private boolean isFacingLeft;

    /**
     * Конструктор врага.
     * Инициализирует все необходимые компоненты для поведения врага.
     * @param x Начальная позиция по X
     * @param y Начальная позиция по Y
     * @param texture Текстура врага
     * @param player Ссылка на игрока для преследования
     * @param walls Массив стен для обработки коллизий
     * @param gameScreen Ссылка на игровой экран
     */
    public Enemy(float x, float y, Texture texture, Player player, Array<GameObject> walls, GameScreen gameScreen) {
        super(x, y, 32, 32, texture);
        this.player = player;
        this.walls = walls;
        this.gameScreen = gameScreen;
        this.velocity = new Vector2();
        this.desiredDirection = new Vector2();
        this.avoidanceForce = new Vector2();
        this.lastPosition = new Vector2(x, y);
        this.stuckTimer = 0;
        this.isStuck = false;
        this.isFacingLeft = false;
        this.patrolTimer = 0;
        this.patrolDirection = new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
        this.isChasing = false;
    }

    /**
     * Обновление состояния врага.
     * Обрабатывает:
     * - Определение режима (преследование/патрулирование)
     * - Движение и коллизии
     * - Атаку игрока
     * @param delta Время с последнего обновления
     */
    @Override
    public void update(float delta) {
        float distanceToPlayer = Vector2.dst(x, y, player.getX(), player.getY());
        isChasing = distanceToPlayer <= VISION_RADIUS && canSeePlayer();

        if (isChasing) {
            updateChasing(delta);
        } else {
            updatePatrol(delta);
        }

        attackTimer += delta;

        if (distanceToPlayer <= ATTACK_RANGE && canSeePlayer()) {
            if (attackTimer >= DAMAGE_COOLDOWN) {
                player.damage(DAMAGE_AMOUNT);
                attackTimer = 0;
                
                if (player.getHealth() <= 0) {
                    player.setDead(true);
                    gameScreen.gameOver();
                }
            }
        }
    }

    /**
     * Обновление режима преследования.
     * Враг активно преследует игрока, избегая препятствия.
     * @param delta Время с последнего обновления
     */
    private void updateChasing(float delta) {
        if (Vector2.dst(x, y, lastPosition.x, lastPosition.y) < 1f) {
            stuckTimer += delta;
            if (stuckTimer > 0.5f) {
                isStuck = true;
            }
        } else {
            stuckTimer = 0;
            isStuck = false;
        }
        
        lastPosition.set(x, y);
        desiredDirection.set(player.getX() - x, player.getY() - y).nor();
        isFacingLeft = player.getX() < x;

        moveWithCollisionAvoidance(delta, SPEED);
    }

    /**
     * Обновление режима патрулирования.
     * Враг случайным образом перемещается по территории.
     * @param delta Время с последнего обновления
     */
    private void updatePatrol(float delta) {
        patrolTimer += delta;
        if (patrolTimer >= PATROL_TIME) {
            patrolTimer = 0;
            patrolDirection.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
        }

        desiredDirection.set(patrolDirection);
        isFacingLeft = patrolDirection.x < 0;

        moveWithCollisionAvoidance(delta, PATROL_SPEED);
    }

    /**
     * Движение с учетом препятствий.
     * Реализует систему избегания стен и других препятствий.
     * @param delta Время с последнего обновления
     * @param speed Скорость движения
     */
    private void moveWithCollisionAvoidance(float delta, float speed) {
        avoidanceForce.setZero();
        for (GameObject wall : walls) {
            float distToWall = Vector2.dst(x, y, wall.getX() + wall.getBounds().width/2, 
                                         wall.getY() + wall.getBounds().height/2);
            if (distToWall < OBSTACLE_AVOIDANCE_RADIUS) {
                Vector2 awayFromWall = new Vector2(x - (wall.getX() + wall.getBounds().width/2),
                                                  y - (wall.getY() + wall.getBounds().height/2));
                awayFromWall.nor().scl(1.0f - distToWall/OBSTACLE_AVOIDANCE_RADIUS);
                avoidanceForce.add(awayFromWall);
            }
        }

        if (isStuck) {
            avoidanceForce.add(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f));
        }

        velocity.set(desiredDirection).add(avoidanceForce).nor();
        
        float newX = x + velocity.x * speed * delta;
        float newY = y + velocity.y * speed * delta;
        
        bounds.setPosition(newX, newY);
        boolean canMove = true;
        
        for (GameObject wall : walls) {
            if (bounds.overlaps(wall.getBounds())) {
                canMove = false;
                break;
            }
        }
        
        if (canMove) {
            x = newX;
            y = newY;
            bounds.setPosition(x, y);
        } else {
            if (!isChasing) {
                patrolDirection.scl(-1);
                patrolTimer = 0;
            }
        }
    }

    /**
     * Отрисовка врага с учетом направления движения.
     * @param batch SpriteBatch для отрисовки
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture,
                  isFacingLeft ? x + width : x,
                  y,
                  isFacingLeft ? -width : width,
                  height);
    }

    /**
     * Проверка видимости игрока.
     * Использует простой алгоритм ray casting для определения,
     * нет ли препятствий между врагом и игроком.
     * @return true если игрок в поле зрения, false если есть препятствия
     */
    private boolean canSeePlayer() {
        float rayStepX = (player.getX() - x) / 20;
        float rayStepY = (player.getY() - y) / 20;
        
        float checkX = x;
        float checkY = y;
        
        for (int i = 0; i < 20; i++) {
            checkX += rayStepX;
            checkY += rayStepY;
            
            for (GameObject wall : walls) {
                if (wall.getBounds().contains(checkX, checkY)) {
                    return false;
                }
            }
        }
        
        return true;
    }
} 