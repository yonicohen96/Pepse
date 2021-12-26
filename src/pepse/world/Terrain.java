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
    private static final float SCALING_RATIO = 2.0f / 9;
    private final NoiseGenerator noiseGenerator;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private final float scalingMaxAmplitude;

    private GameObjectCollection gameObjects;
    private int groundLayer;
    private final float groundHeightAtX0;

    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * 2 / 3;
        this.scalingMaxAmplitude = windowDimensions.y() * SCALING_RATIO;
        this.noiseGenerator = new NoiseGenerator(seed);
    }

    public float groundHeightAt(float x) {
        return (float) (groundHeightAtX0 + (scalingMaxAmplitude * noiseGenerator.noise(x)));
    }

    public void createInRange(int minX, int maxX) {
        int nextX = getMinX(minX);
        while(nextX <= maxX){
            // create blocks in X:
            createTerrainColumn(nextX);
            nextX += Block.SIZE;
        }
    }

    private void createTerrainColumn(int nextX) {
        int yHeight = (int) (Math.floor(groundHeightAt(nextX) / Block.SIZE) * Block.SIZE);
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            createBlockAtXY(nextX, yHeight);
            yHeight += Block.SIZE;
        }
    }

    private void createBlockAtXY(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
        GameObject gameObject = new GameObject(new Vector2(blockX, blockY), new Vector2(Block.SIZE, Block.SIZE), renderable);
        gameObject.setTag(GROUND_TAG);
        gameObjects.addGameObject(gameObject);
    }

    private int getMinX(int minX){
        if (minX >= 0){
            return (minX / Block.SIZE) * Block.SIZE;
        }
        return ((minX / Block.SIZE) - 1) * Block.SIZE;
    }
    
    


}
