package pepse.world.trees;

import pepse.world.Block;
import pepse.world.ScreenRendererManager;
import pepse.world.Terrain;

import java.util.Objects;
import java.util.Random;

/**
 * class that represents a tree objects manager for pepse game
 */
public class Tree {

    private static final int STEM_BLOCKS_NUMBER_RANGE = 9;
    private static final int MIN_STEM_BLOCK_NUMBER = 6;
    private static final int MAX_RANDOM_VALUE = 100;
    private static final int TREE_DENSITY_PERCENTAGE = 3;
    private final Terrain terrain;
    private final int seed;
    private final LeavesProducer leavesProducer;
    private final StemProducer stemProducer;

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
        this.leavesProducer = new LeavesProducer(seed, rendererManager, leafLayer);
        this.stemProducer = new StemProducer(rendererManager, stemLayer);
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
        stemProducer.createStem(x, treeBaseY, treeSize);
        leavesProducer.createLeaves(x, treeBaseY - treeSize * Block.SIZE, treeSize);
    }


    /*
    function to allocate a tree in a given x coordinate
     */
    private boolean allocateTreeInX(int x){
        Random treeAllocateRand = new Random(Objects.hash(x * seed));
        return treeAllocateRand.nextInt(MAX_RANDOM_VALUE) <= TREE_DENSITY_PERCENTAGE;
    }

}
