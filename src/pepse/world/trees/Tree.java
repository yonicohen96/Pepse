package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.ScreenRendererManager;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * class that represents a tree objects manager for pepse game
 */
public class Tree {
    private static final String STEM_BLOCK_TAG = "stem block";
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final int STEM_BLOCKS_NUMBER_RANGE = 9;
    private static final int MIN_STEM_BLOCK_NUMBER = 6;
    private static final int MAX_RANDOM_VALUE = 100;
    private static final int TREE_DENSITY_PERCENTAGE = 3;
    private final Terrain terrain;
    private final int stemLayer;
    private final ScreenRendererManager rendererManager;
    private final int seed;
    private final LeavesCreator leavesCreator;

    /**
     * constructor
     * @param terrain Terrain object - to locate trees above the terrain
     * @param seed random factor seed
     * @param stemLayer layer allocated for stem
     * @param leafLayer layer allocated for leaves
     * @param rendererManager renderer manager object
     */
    public Tree(Terrain terrain, int seed, int stemLayer, int leafLayer,
                ScreenRendererManager rendererManager) {
        this.terrain  = terrain;
        this.seed = seed;
        this.stemLayer = stemLayer;
        this.rendererManager = rendererManager;
        this.leavesCreator = new LeavesCreator(seed, rendererManager, leafLayer);
    }

    /**
     * function to create trees in given range
     * @param minX left x border
     * @param maxX right x border
     */
    public void createInRange(int minX, int maxX) {
        int nextX = Block.roundToBlock(minX);
        while(nextX <= maxX){
            // create blocks in X:
            if (allocateTreeInX(nextX)){
                createTree(nextX);
            }
            nextX += Block.SIZE;
        }
    }

    /*
    function to create a single tree
     */
    private void createTree(int x) {
        int treeBaseY = (int) terrain.groundHeightAt(x) - Block.SIZE;
        int treeSize = (new Random(Objects.hash(x * seed)).nextInt(STEM_BLOCKS_NUMBER_RANGE * Block.SIZE) +
                MIN_STEM_BLOCK_NUMBER * Block.SIZE) / Block.SIZE;
        createStem(x, treeBaseY, treeSize);
        leavesCreator.createLeaves(x, treeBaseY - treeSize * Block.SIZE, treeSize);
    }


    /*
    function to create a single tree's stem
     */
    private void createStem(int x, int treeBaseY, int treeSize) {
        for (int i = 0; i < treeSize; i++) {
            createStemBlock(x, treeBaseY);
            treeBaseY -= Block.SIZE;
        }
    }

    /*
    function to create a stem block
     */
    private void createStemBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(STEM_COLOR);
        GameObject gameObject = new Block(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(STEM_BLOCK_TAG);
        rendererManager.addGameObject(gameObject, stemLayer);
    }

    /*
    function to allocate a tree in a given x coordinate
     */
    private boolean allocateTreeInX(int x){
        Random treeAllocateRand = new Random(Objects.hash(x * seed));
        return treeAllocateRand.nextInt(MAX_RANDOM_VALUE) <= TREE_DENSITY_PERCENTAGE;
    }

}
