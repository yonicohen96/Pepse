package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;

import java.awt.*;
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
        GameObject sun = Sun.create(gameObjects(), Layer.BACKGROUND, windowController.getWindowDimensions(), CYCLE_LENGTH);
        // todo check if this down-casting is ok (sending sun which is GameObject to Sun parameter)
        GameObject sunHalo = SunHalo.create(gameObjects(), Layer.BACKGROUND, sun, new Color(255, 255, 0, 20));

    }


}
