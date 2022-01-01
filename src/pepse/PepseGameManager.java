package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.ScreenRendererManager;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

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
    private static final int AVATAR_LAYER = Layer.BACKGROUND + 4;
    private ScreenRendererManager screenRendererManager;
    private int screenLeftX;
    private int screenRightX;
    private int deltaScreen;

    private LinkedList<GameObject[]> screenFrames;
    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;


    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        deltaScreen = (int)windowController.getWindowDimensions().x();
        screenRendererManager = new ScreenRendererManager(gameObjects(),
                new LinkedList<ArrayList<Map.Entry<GameObject, Integer>>>());
        screenRendererManager.setIndexToFill(1);
        GameObject sky = Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
        terrain = new Terrain(gameObjects(), LOWER_GROUND_LAYER, UPPER_GROUND_LAYER, windowController.getWindowDimensions(),
                SEED, screenRendererManager);
        terrain.createInRange(0, (int)windowController.getWindowDimensions().x());
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(), CYCLE_LENGTH);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(), CYCLE_LENGTH);
        // todo check if this down-casting is ok (sending sun which is GameObject to Sun parameter)
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, new Color(255, 255, 0, 20));
        // todo check if should instantiate only one tree or instance for each tree
        tree = new Tree(terrain, gameObjects(), STEM_LAYER, LEAF_LAYER, screenRendererManager);
        tree.createInRange(0, (int)windowController.getWindowDimensions().x());
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(STEM_LAYER, AVATAR_LAYER, true);
        // create avatar
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, Vector2.ZERO, inputListener, imageReader);
        //avatar.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        float avatarX = windowController.getWindowDimensions().x() / 2;
        float avatarY = terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) -
                Block.SIZE - avatar.getDimensions().y();
        Vector2 initialAvatarLocation = new Vector2(avatarX, avatarY);
        avatar.setTopLeftCorner(initialAvatarLocation);

        setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(0.5f).add(initialAvatarLocation.mult(-1))
                ,windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, UPPER_GROUND_LAYER, true);

        screenRendererManager.setIndexToFill(0);
        tree.createInRange(-(int)windowController.getWindowDimensions().x(),0);
        terrain.createInRange(-(int)windowController.getWindowDimensions().x(),0);
        screenRendererManager.setIndexToFill(2);
        tree.createInRange((int)windowController.getWindowDimensions().x(),2*(int)windowController.getWindowDimensions().x());
        terrain.createInRange((int)windowController.getWindowDimensions().x(),2*(int)windowController.getWindowDimensions().x());

        screenLeftX = 0;
        screenRightX = (int) windowController.getWindowDimensions().x();
        //todo : work with products of block size, maybe define the delta by the initial blocks width.


        // todo - delete
        Camera objCamera = new Camera(avatar, Vector2.ZERO,
        windowController.getWindowDimensions().mult(8f),
        windowController.getWindowDimensions());

        this.setCamera(objCamera);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        checkBoundaries();
        if (avatar.getVelocity().y() > 0){
            float avatarX = avatar.getTopLeftCorner().x();
            float terrainAtX = (int) (Math.floor(terrain.groundHeightAt(avatarX) / Block.SIZE) * Block.SIZE);
            float avatarTopLeftY = terrainAtX - avatar.getDimensions().y();
            avatar.transform().setTopLeftCornerY(Math.min(avatar.getTopLeftCorner().y(), avatarTopLeftY));
        }
    }

    private void checkBoundaries() {
        // check if need to change left

        if(avatar.getCenter().x() > screenRightX){
            screenRendererManager.removeGameObjects(0);
            screenLeftX = screenRightX;
            screenRightX += deltaScreen;
            terrain.createInRange(screenRightX, screenRightX + deltaScreen);
            tree.createInRange(screenRightX, screenRightX + deltaScreen);
            System.out.printf("%d, %d\n",screenLeftX,screenRightX);
            // todo add an option to view the world in wide resolution zoom out.]
            System.out.printf("%d\n ",screenRendererManager.gameObjectsList.size());
            System.out.printf("%d,%d, %d\n",screenRendererManager.gameObjectsList.get(0).size(),screenRendererManager.gameObjectsList.get(1).size(),screenRendererManager.gameObjectsList.get(2).size());
        }
        if(avatar.getCenter().x() < screenLeftX){
            screenRendererManager.removeGameObjects(2);
            screenRightX = screenLeftX;
            screenLeftX -= deltaScreen;
            terrain.createInRange(screenLeftX - deltaScreen, screenLeftX);
            tree.createInRange(screenLeftX - deltaScreen, screenLeftX);
        }




    }


}
