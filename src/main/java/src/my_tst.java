package src;

import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import org.mockito.Mockito;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.*;
import danogl.util.Counter;
import danogl.util.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import src.gameobjects.Ball;
import src.gameobjects.Brick;
import src.gameobjects.Paddle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrickerGameManagerTest {

    private static final int BRICKS_PER_ROW = 5;
    private static final int BRICKS_PER_COL = 8;
    private BrickerGameManager gameManager;

    @Mock
    private ImageReader imageReader;
    @Mock
    private SoundReader soundReader;
    @Mock
    private UserInputListener inputListener;
    @Mock
    private WindowController windowController;
    @Mock
    private GameObjectCollection gameObjects;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameManager = new BrickerGameManager("Test Game", new Vector2(700, 500));

        // Mock behavior
        when(windowController.getWindowDimensions()).thenReturn(new Vector2(700, 500));
        when(imageReader.readImage(anyString(), anyBoolean())).thenReturn((ImageRenderable) mock(Renderable.class));
        when(soundReader.readSound(anyString())).thenReturn(mock(Sound.class));

        // Inject mocked GameObjectCollection
        try {
            java.lang.reflect.Field gameObjectsField = GameManager.class.getDeclaredField("gameObjects");
            gameObjectsField.setAccessible(true);
            gameObjectsField.set(gameManager, gameObjects);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInitialization() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        verify(gameObjects, atLeastOnce()).addGameObject(any(GameObject.class), anyInt());
        verify(windowController).setTargetFramerate(80);
    }

    @Test
    void testBallCreation() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        verify(gameObjects).addGameObject(argThat(obj -> obj instanceof Ball));
    }

    @Test
    void testPaddleCreation() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        verify(gameObjects).addGameObject(argThat(obj -> obj instanceof Paddle));
    }

    @Test
    void testBrickCreation() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        verify(gameObjects, atLeast(BRICKS_PER_ROW * BRICKS_PER_COL)).addGameObject(argThat(obj -> obj instanceof Brick), anyInt());
    }

    @Test
    void testLoseLife() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        // Simulate ball going out of bounds
        gameManager.ball.setCenter(new Vector2(350, 501));
        int initialLives = gameManager.counterLives.value();

        gameManager.update(0.1f);

        assertEquals(initialLives - 1, gameManager.counterLives.value());
    }

    @Test
    void testWinCondition() {
        gameManager.initializeGame(imageReader, soundReader, inputListener, windowController);

        // Simulate all bricks destroyed
        gameManager.counterBricks.reset();

        gameManager.update(0.1f);

        verify(windowController).openYesNoDialog(contains("You win!"));
    }

    // Add more tests as needed
}