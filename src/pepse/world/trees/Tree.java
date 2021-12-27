package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

public class Tree {
    private static final double TREE_SEED = 13;
    private static final String STEM_BLOCK_TAG = "stem block";
    private static NoiseGenerator noiseGenerator = new NoiseGenerator(TREE_SEED);
    private final Terrain terrain;
    private final static Random random = new Random();
    private final GameObjectCollection gameObjects;

    public Tree(Terrain terrain, GameObjectCollection gameObjects) {
        this.terrain  = terrain;
        this.gameObjects = gameObjects;
    }

    public void createInRange(int minX, int maxX) {
        int nextX = getMinX(minX);
        while(nextX <= maxX){
            // create blocks in X:
            if (allocateTreeInX(nextX)){
                createTree(nextX);
            }
            nextX += Block.SIZE;
        }
    }

    private void createTree(int x) {
        int treeBaseY = (int) terrain.groundHeightAt(x) - Block.SIZE;
        int treeSize = (random.nextInt(treeBaseY * 2 / 3) + treeBaseY / 3) / Block.SIZE;
        for (int i = 0; i < treeSize; i++) {
            System.out.println(treeSize);
            createStemBlock(x, treeBaseY);
            treeBaseY -= Block.SIZE;

        }
    }

    private void createStemBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(new Color(100, 50, 20));
        GameObject gameObject = new Block(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(STEM_BLOCK_TAG);
        gameObjects.addGameObject(gameObject);
    }

    private boolean allocateTreeInX(int x){
        double noiseVal = noiseGenerator.noise(x);
        int thirdDigit = getNDigit(noiseVal, 3);
        int forthDigit = getNDigit(noiseVal, 4);
        return thirdDigit == 3 && forthDigit > 4;
    }

    private int getNDigit(double num, int n){
        return  (int) (num * Math.pow(10, n)) % 10;
    }

    private int getMinX(int minX){
        if (minX >= 0){
            return (minX / Block.SIZE) * Block.SIZE;
        }
        return ((minX / Block.SIZE) - 1) * Block.SIZE;
    }
}
