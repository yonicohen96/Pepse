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
import java.util.LinkedList;

/**
 * Class of the game manager, it initializes all the world objects, and handles all the logic behind creating
 * and removing the extended screen renderables.
 */
public class PepseGameManager extends GameManager {
    private static final int SEED = 100;
    private static final float CYCLE_LENGTH = 30;
    private static final int SCREEN_BUFFER_SIZE = 3;
    private static final int SUN_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int STEM_LAYER = Layer.BACKGROUND + 3;
    private static final int LEAF_LAYER = Layer.BACKGROUND + 4;
    private static final int LOWER_GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int UPPER_GROUND_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int AVATAR_LAYER = Layer.BACKGROUND + 4;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final float CAMERA_FACTOR = 0.5f;
    private ScreenRendererManager screenRendererManager;
    private int screenLeftX;
    private int screenRightX;
    private int deltaScreen;
    private Avatar avatar;
    private Terrain terrain;
    private Tree tree;

    /**
     * main function of the program, starts the game by calling the run method
     * @param args
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    /**
     * initializes all the GameObjects (world objects) using the given arguments, and creating the
     * screenRenderManager that controls the extended screen renderables
     * @param imageReader object that helps us to create the renderables of the GameObjects
     * @param soundReader object that helps us to create the sounds of the GameObjects
     * @param inputListener responsible for getting the input from the user
     * @param windowController responsible for getting relevant info about the game screen
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        setGameProperties(windowController);
        createWorldObjects(imageReader, inputListener, windowController);
        renderScreens(windowController);
        setLayersCollisions();

//        Camera objCamera = new Camera(avatar, Vector2.ZERO,
//        windowController.getWindowDimensions().mult(8f),
//        windowController.getWindowDimensions());
//        this.setCamera(objCamera);
    }

/*
    responsible for creating all the world GameObjects
 */
    private void createWorldObjects(ImageReader imageReader, UserInputListener inputListener,
                                    WindowController windowController) {
        initializeTerrain(windowController);
        initializeSky(windowController);
        initializeDayNight(windowController);
        initializeTrees();
        initializeAvatar(imageReader, inputListener, windowController);
    }
/*
    define general variables that using during the game run, the delta screen, that define the extended
    screens, and the screenRendererManager that controls the extended screen renderables
 */
    private void setGameProperties(WindowController windowController) {
        deltaScreen = (int) windowController.getWindowDimensions().x();
        screenRendererManager = new ScreenRendererManager(gameObjects(),
                new LinkedList<>(), SCREEN_BUFFER_SIZE);
    }
/*
    sets all the collisions between different GameObjects by their layers
 */
    private void setLayersCollisions() {
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, STEM_LAYER, false);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, UPPER_GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, STEM_LAYER, true);
    }
/*
    initialize all the renderables (all the world GameObjects) of the extended screens to the
    gameObjectCollection and to the screenRenderManager
 */
    private void renderScreens(WindowController windowController) {
        screenLeftX = 0;
        screenRightX = (int) windowController.getWindowDimensions().x();
        for (int i = 0; i < SCREEN_BUFFER_SIZE; i++) {
            screenRendererManager.setIndexToFill(i);
            tree.createInRange(-deltaScreen + i * deltaScreen, i * deltaScreen);
            terrain.createInRange(-deltaScreen + i * deltaScreen, i * deltaScreen);
        }
    }
/*
    initialize an instance of the game avatar and all its relevant fields and attributes
 */
    private void initializeAvatar(ImageReader imageReader, UserInputListener inputListener,
                                  WindowController windowController) {
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, Vector2.ZERO, inputListener, imageReader);
        float avatarX = windowController.getWindowDimensions().x() / 2;
        float avatarY = terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) -
                Block.SIZE - avatar.getDimensions().y();
        Vector2 initialAvatarLocation = new Vector2(avatarX, avatarY);
        avatar.setTopLeftCorner(initialAvatarLocation);
        setCamera(new Camera(avatar, windowController.getWindowDimensions().mult(CAMERA_FACTOR).
                add(initialAvatarLocation.mult(-1))
                , windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }
/*
    initialize an instance of the Terrain that creates all the terrain blocks of the game
 */
    private void initializeTerrain(WindowController windowController) {
        terrain = new Terrain(gameObjects(), LOWER_GROUND_LAYER, UPPER_GROUND_LAYER,
                windowController.getWindowDimensions(),
                SEED, screenRendererManager);
    }
/*
    initialize an instance of the Sky class to the game
 */
    private void initializeSky(WindowController windowController) {
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);
    }
/*
    initialize an instance of the Tree class that creates all the trees in the game (stems and leaves)
 */
    private void initializeTrees() {
        tree = new Tree(terrain, gameObjects(), STEM_LAYER, LEAF_LAYER, screenRendererManager);
    }
/*
    initialize all the day and night GameObjects to the game (night, sun, sunHalo)
 */
    private void initializeDayNight(WindowController windowController) {
        Night.create(gameObjects(), Layer.FOREGROUND, windowController.getWindowDimensions(),
                CYCLE_LENGTH / 2);
        GameObject sun = Sun.create(gameObjects(), SUN_LAYER, windowController.getWindowDimensions(),
                CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
    }
    /**
     * Should be called once per frame.
     * responsible for the fluent checks of the game, updating the extended screen renderables and defining
     * the avatar location while falling into the ground.
     * Params:
     * deltaTime – The time elapsed, in seconds, since the last frame.
     */
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
/*
    checks for the avatar position regarding the screen edges, to determinate if we need to change the
    extended screen and its renderables.
 */
    private void checkBoundaries() {
        // check if need to change left
        if(avatar.getCenter().x() > screenRightX){
            screenRendererManager.removeGameObjects(0);
            screenLeftX = screenRightX;
            screenRightX += deltaScreen;
            terrain.createInRange(screenRightX, screenRightX + deltaScreen);
            tree.createInRange(screenRightX, screenRightX + deltaScreen);
        }
        if(avatar.getCenter().x() < screenLeftX){
            screenRendererManager.removeGameObjects(SCREEN_BUFFER_SIZE - 1);
            screenRightX = screenLeftX;
            screenLeftX -= deltaScreen;
            terrain.createInRange(screenLeftX - deltaScreen, screenLeftX);
            tree.createInRange(screenLeftX - deltaScreen, screenLeftX);
        }
    }


}
