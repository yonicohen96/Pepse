package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.nio.channels.SelectionKey;

public class PepseGameManager extends GameManager {
    private static final int SEED = 100;
    private static final float CYCLE_LENGTH = 30;
    // todo reorder layer
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int STEM_LAYER = Layer.BACKGROUND + 3;
    private static final int LEAF_LAYER = Layer.BACKGROUND + 4;
    private static final int LOWER_GROUND_LAYER = Layer.BACKGROUND + 5;
    private static final int UPPER_GROUND_LAYER = Layer.BACKGROUND + 6;
    private static final int AVATAR_LAYER = Layer.BACKGROUND + 7;

    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);{
            Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        }
        Terrain terrain = new Terrain(gameObjects(), LOWER_GROUND_LAYER, UPPER_GROUND_LAYER, windowController.getWindowDimensions(),
                SEED);
        terrain.createInRange(0, (int)windowController.getWindowDimensions().x());
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        // todo check if this down-casting is ok (sending sun which is GameObject to Sun parameter)
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, new Color(255, 255, 0, 20));
        // todo check if should instantiate only one tree or instance for each tree
        Tree tree = new Tree(terrain, gameObjects(), STEM_LAYER, LEAF_LAYER);
        tree.createInRange(0, (int)windowController.getWindowDimensions().x());
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, UPPER_GROUND_LAYER, true);
        // create avatar
        Avatar avatar = Avatar.create(gameObjects(), AVATAR_LAYER, Vector2.ZERO, inputListener, imageReader);
        float avatarX = windowController.getWindowDimensions().x() / 2;
        float avatarY = terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) -
                Block.SIZE - avatar.getDimensions().y();
        avatar.setTopLeftCorner(new Vector2(avatarX, avatarY));


    }


}
