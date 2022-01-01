package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;

public class Terrain {

    private static final String GROUND_TAG = "ground";
    private static final float SCALING_RATIO = 4.0f / 9;
    private static final float X0_HEIGHT_RATIO = 2.0f / 3;
    private static final double STRETCH_NOISE = 0.05;
    private final NoiseGenerator noiseGenerator;
    private int seed;
    private ScreenRendererManager rendererManager;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 30;
    private final float scalingMaxAmplitude;
    private final GameObjectCollection gameObjects;
    private final int lowerGroundLayer;
    private final int upperGroundLayer;
    private final float groundHeightAtX0;

    public Terrain(GameObjectCollection gameObjects,
                   int lowerGroundLayer, int upperGroundLayer, Vector2 windowDimensions,
                   int seed, ScreenRendererManager rendererManager){
        this.gameObjects = gameObjects;
        this.upperGroundLayer = upperGroundLayer;
        this.lowerGroundLayer = lowerGroundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * X0_HEIGHT_RATIO;
        this.scalingMaxAmplitude = windowDimensions.y() * SCALING_RATIO;
        this.noiseGenerator = new NoiseGenerator(seed);
        this.seed = seed;
        this.rendererManager = rendererManager;
    }

    public float groundHeightAt(float x) {
        return (float) (groundHeightAtX0 + (scalingMaxAmplitude * noiseGenerator.noise(STRETCH_NOISE * x)));
    }

    public void createInRange(int minX, int maxX) {
        int nextX = Block.roundToBlock(minX);
        while(nextX <= maxX){
            // create blocks in X:
            createTerrainColumn(nextX);
            nextX += Block.SIZE;
        }
    }

    private void createTerrainColumn(int nextX) {
        int yHeight = (int) (Math.floor(groundHeightAt(nextX) / Block.SIZE) * Block.SIZE);
        createBlockAtXY(nextX, yHeight, upperGroundLayer);
        yHeight += Block.SIZE;
        for (int i = 1; i < TERRAIN_DEPTH; i++) {
            createBlockAtXY(nextX, yHeight, lowerGroundLayer);
            yHeight += Block.SIZE;
        }
    }

    private void createBlockAtXY(int blockX, int blockY, int layer) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        GameObject gameObject = new Block(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(GROUND_TAG);
        gameObjects.addGameObject(gameObject, layer);
        rendererManager.addGameObject(gameObject, layer);
    }

}
