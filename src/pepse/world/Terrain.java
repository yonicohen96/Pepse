package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

/**
 * class that represents a terrain object of pepse game
 */
public class Terrain {
    private static final String GROUND_TAG = "ground";
    private static final float SCALING_RATIO = 4.0f / 9;
    private static final float X0_HEIGHT_RATIO = 2.0f / 3;
    private static final double STRETCH_NOISE = 0.05;
    private final NoiseGenerator noiseGenerator;
    private ScreenRendererManager rendererManager;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 30;
    private final float scalingMaxAmplitude;
    private final int lowerGroundLayer;
    private final int upperGroundLayer;
    private final float groundHeightAtX0;

    /**
     * constructor of Terrain
     * @param gameObjects game object collection
     * @param groundLayer layer for terrain's higher objects
     * @param windowDimensions window's dimensions
     * @param seed seed to be used for generating terrain's block
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer,
                   Vector2 windowDimensions, int seed){
        this.upperGroundLayer = groundLayer;
        this.lowerGroundLayer = upperGroundLayer - 1;
        this.groundHeightAtX0 = windowDimensions.y() * X0_HEIGHT_RATIO;
        this.scalingMaxAmplitude = windowDimensions.y() * SCALING_RATIO;
        this.noiseGenerator = new NoiseGenerator(seed);
    }

    /**
     * function to get the height of the ground at given x coordinate
     * @param x the x coordinate to get the ground height at
     * @return the height at the given x coordinate
     */
    public float groundHeightAt(float x) {
        return (float) (groundHeightAtX0 + (scalingMaxAmplitude * noiseGenerator.noise(STRETCH_NOISE * x)));
    }

    /**
     * set render manager for the game
     * @param screenRendererManager screen render manager object
     */
    public void setRendererManager(ScreenRendererManager screenRendererManager){
        this.rendererManager = screenRendererManager;
    }

    /**
     * function to create terrain blocks in a given range
     * @param minX left border for blocks' creation
     * @param maxX right border for blocks' creation
     */
    public void createInRange(int minX, int maxX) {
        int nextX = Block.roundToBlock(minX);
        while(nextX <= maxX){
            // create blocks in X:
            createTerrainColumn(nextX);
            nextX += Block.SIZE;
        }
    }

    /*
    create a column of terrain blocks in a given x coordinate
     */
    private void createTerrainColumn(int nextX) {
        int yHeight = (int) (Math.floor(groundHeightAt(nextX) / Block.SIZE) * Block.SIZE);
        createBlockAtXY(nextX, yHeight, upperGroundLayer);
        yHeight += Block.SIZE;
        for (int i = 1; i < TERRAIN_DEPTH; i++) {
            createBlockAtXY(nextX, yHeight, lowerGroundLayer);
            yHeight += Block.SIZE;
        }
    }

    /*
    function to create a terrain block at given x,y coordinates
     */
    private void createBlockAtXY(int blockX, int blockY, int layer) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        GameObject gameObject = new Block(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(GROUND_TAG);
        rendererManager.addGameObject(gameObject, layer);
    }

}
