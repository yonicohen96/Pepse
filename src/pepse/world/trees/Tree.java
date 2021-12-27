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
    private static final double TREE_SEED = 14;
    private static final String STEM_BLOCK_TAG = "stem block";
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final Color LEAVES_COLOR = new Color(50, 200, 30);
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
        createStem(x, treeBaseY, treeSize);
        createLeaves(x, treeBaseY - treeSize * Block.SIZE, treeSize);
    }

    private void createLeaves(int x, int treetop, int treeSize) {
        int delta = getMinX(treeSize * Block.SIZE / 3);
        for (int i = x - delta ; i <= x + delta; i+=Block.SIZE) {
            for (int j = treetop - delta; j <= treetop + delta ; j+=Block.SIZE) {
                if (random.nextInt(10) < 8){
                    createLeavesBlock(i, j);
                }
            }
        }

    }

    private void createStem(int x, int treeBaseY, int treeSize) {
        for (int i = 0; i < treeSize; i++) {
            System.out.println(treeSize);
            createStemBlock(x, treeBaseY);
            treeBaseY -= Block.SIZE;
        }
    }

    // todo unite all function of creations of blocks
    private void createLeavesBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(LEAVES_COLOR);
        GameObject gameObject = new Leaf(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(STEM_BLOCK_TAG);
        gameObjects.addGameObject(gameObject);
    }

    private void createStemBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(STEM_COLOR);
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
