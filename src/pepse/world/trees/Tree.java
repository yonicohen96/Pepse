package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.ScreenRendererManager;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

public class Tree {
    private static final double TREE_SEED = 14;
    private static final String STEM_BLOCK_TAG = "stem block";
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final Color LEAVES_COLOR = new Color(50, 200, 30);
    private static final float LEAF_SCALE_DELTA = 0.5f;
    private static final float FADEOUT_TIME = 10f;
    private static final int LEAF_DEATH_RANGE = 5;
    private static final int LEAF_LIVE_RANGE = 500;
    private static NoiseGenerator noiseGenerator = new NoiseGenerator(TREE_SEED);
    private final Terrain terrain;
    private final static Random random = new Random();
    private final GameObjectCollection gameObjects;
    private int stemLayer;
    private int leafLayer;
    private ScreenRendererManager rendererManager;

    public Tree(Terrain terrain, GameObjectCollection gameObjects, int stemLayer, int leafLayer,
                ScreenRendererManager rendererManager) {
        this.terrain  = terrain;
        this.gameObjects = gameObjects;
        this.stemLayer = stemLayer;
        this.leafLayer = leafLayer;
        this.rendererManager = rendererManager;
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
                    // todo - set more reasonable constants for leaves
                }
            }
        }
    }

    private void createStem(int x, int treeBaseY, int treeSize) {
        for (int i = 0; i < treeSize; i++) {
            //System.out.println(treeSize);
            createStemBlock(x, treeBaseY);
            treeBaseY -= Block.SIZE;
        }
    }

    // todo unite all function of creations of blocks
    private void createLeavesBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(LEAVES_COLOR);
        Leaf leaf = new Leaf(new Vector2(blockX, blockY), renderable);
        startLifeCycle(leaf);

        // todo check if need to save states for leaves - if not fallen,
        float windRandWaitTime = (random.nextInt(50)/ 5f);
        new ScheduledTask(leaf, windRandWaitTime, false
                , () -> windLeafActions(leaf));
        leaf.setTag(STEM_BLOCK_TAG);
        gameObjects.addGameObject(leaf, leafLayer);
        rendererManager.addGameObject(leaf, leafLayer);
    }

    private void startLifeCycle(Leaf leaf) {
        leaf.resetPosition();
        float lifeRandWaitTime = (random.nextInt(LEAF_LIVE_RANGE)/ 5f);
        new ScheduledTask(leaf, lifeRandWaitTime, false
                , () -> leafLifeCycleAction(leaf));
    }

    private void leafLifeCycleAction(Leaf leaf) {
        // TODO change for sinus fall
        leaf.transform().setVelocityY(50);
        leaf.renderer().fadeOut(FADEOUT_TIME, () -> postFadeActions(leaf));
    }

    private void postFadeActions(Leaf leaf) {
        float deathTime = (float) random.nextInt(LEAF_DEATH_RANGE);
        new ScheduledTask(leaf, deathTime, false, () -> startLifeCycle(leaf));
    }

    private void createStemBlock(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(STEM_COLOR);
        GameObject gameObject = new Block(new Vector2(blockX, blockY), renderable);
        gameObject.setTag(STEM_BLOCK_TAG);
        gameObjects.addGameObject(gameObject, stemLayer);
        rendererManager.addGameObject(gameObject, stemLayer);
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

    private void windLeafActions(GameObject leaf){
        new Transition<Float>(
                leaf, // the game object being changed
                (angle) -> leaf.renderer().setRenderableAngle(angle), // the method to call
                (float) (-5 * Math.PI), // initial transition value
                (float) (5 * Math.PI), // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                1.1f, // transition sun
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        new Transition<Float>(
                leaf, // the game object being changed
                (value) -> leaf.setDimensions(changeBounds(value, leaf.getDimensions().x())), // the method to call
                -LEAF_SCALE_DELTA, // initial transition value
                LEAF_SCALE_DELTA, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                0.8f, // transition sun
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

    private Vector2 changeBounds(Float value, float x) {
        float size =  x + value;
        if (size < 20){
            size = 20;
        }
        if (size > 40){
            size = 40;
        }
        return new Vector2(size, size);
    }


}
