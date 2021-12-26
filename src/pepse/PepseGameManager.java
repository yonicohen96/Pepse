package pepse;

import danogl.GameManager;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;

import java.nio.channels.SelectionKey;

public class PepseGameManager extends GameManager {

    private static final int SEED = 100;
    private static final float CYCLE_LENGTH = 30;

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);{
            Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        }
        Terrain terrain = new Terrain(gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(),
                SEED);
        terrain.createInRange(0, (int)windowController.getWindowDimensions().x());
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), CYCLE_LENGTH);
    }
}
