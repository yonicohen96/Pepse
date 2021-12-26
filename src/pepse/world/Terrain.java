package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.util.NoiseGenerator;

public class Terrain {

    private final NoiseGenerator noise;
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private final float groundHeightAtX0;

    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.groundHeightAtX0 = windowDimensions.y() * 2 / 3;
        this.noise = new NoiseGenerator(seed);
    }

    public float groundHeightAt(float x) {
        return (float) noise.noise(x);

    }
}
