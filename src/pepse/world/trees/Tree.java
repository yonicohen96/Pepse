package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.ScreenRendererManager;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Tree {
    private static final String STEM_BLOCK_TAG = "stem block";
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private static final Color LEAVES_COLOR = new Color(50, 200, 30);
    private static final float LEAF_SCALE_DELTA = 0.5f;
    private static final float FADEOUT_TIME = 10f;
    private static final int LEAF_DEATH_RANGE = 5;
    private static final int LEAF_LIVE_RANGE = 500;
    private static final int STEM_BLOCKS_NUMBER_RANGE = 9;
    private static final int MIN_STEM_BLOCK_NUMBER = 6;
    private static final int MAX_LEAVES_CREATION_RANK = 100;
    private static final int LEAVES_CREATION_RANK = 80;
    private static final int LEAF_FALL_RANGE = 50;
    private static final float LEAF_RANK_RATIO = 5f;
    private static final int LEAF_FALL_VELOCITY = 50;
    private static final int MAX_RANDOM_VALUE = 100;
    private static final int TREE_DENSITY_PERCENTAGE = 5;
    private static final int WIND_TRANSITION_RANGE = 5;
    private static final float LEAF_ROTATE_TRANSITION_TIME = 1.1f;
    private static final float LEAF_SCALE_TRANSITION_TIME = 0.8f;
    private static final int LEAF_MIN_SIZE = 27;
    private static final int LEAF_MAX_SIZE = 40;
    private static final int GENERAL_SEED = 60;
    private final Terrain terrain;
    private final static Random random = new Random();
    private final GameObjectCollection gameObjects;
    private final int stemLayer;
    private final int leafLayer;
    private final ScreenRendererManager rendererManager;

    public Tree(Terrain terrain, GameObjectCollection gameObjects, int stemLayer, int leafLayer,
                ScreenRendererManager rendererManager) {
        this.terrain  = terrain;
        this.gameObjects = gameObjects;
        this.stemLayer = stemLayer;
        this.leafLayer = leafLayer;
        this.rendererManager = rendererManager;
    }

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

    private void createTree(int x) {
        int treeBaseY = (int) terrain.groundHeightAt(x) - Block.SIZE;
        int treeSize = (new Random(x).nextInt(STEM_BLOCKS_NUMBER_RANGE * Block.SIZE) +
                MIN_STEM_BLOCK_NUMBER * Block.SIZE) / Block.SIZE;
        createStem(x, treeBaseY, treeSize);
        createLeaves(x, treeBaseY - treeSize * Block.SIZE, treeSize);
    }

    private void createLeaves(int x, int treetop, int treeSize) {
        Random leavesRandom = new Random(x);
        int delta = Block.roundToBlock((treeSize * Block.SIZE / 3));
        for (int i = x - delta ; i <= x + delta; i+=Block.SIZE) {
            for (int j = treetop - delta; j <= treetop + delta ; j+=Block.SIZE) {
                if (leavesRandom.nextInt(MAX_LEAVES_CREATION_RANK) < LEAVES_CREATION_RANK){
                    createLeaf(i, j);
                }
            }
        }
    }

    private void createStem(int x, int treeBaseY, int treeSize) {
        for (int i = 0; i < treeSize; i++) {
            createStemBlock(x, treeBaseY);
            treeBaseY -= Block.SIZE;
        }
    }

    private void createLeaf(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(LEAVES_COLOR);
        Leaf leaf = new Leaf(new Vector2(blockX, blockY), renderable);
        startLifeCycle(leaf);
        float windRandWaitTime = (random.nextInt(LEAF_FALL_RANGE)/ LEAF_RANK_RATIO);
        new ScheduledTask(leaf, windRandWaitTime, false
                , () -> windLeafActions(leaf));
        leaf.setTag(STEM_BLOCK_TAG);
        gameObjects.addGameObject(leaf, leafLayer);
        rendererManager.addGameObject(leaf, leafLayer);
    }

    private void startLifeCycle(Leaf leaf) {
        leaf.resetPosition();
        float lifeRandWaitTime = (random.nextInt(LEAF_LIVE_RANGE)/ LEAF_RANK_RATIO);
        new ScheduledTask(leaf, lifeRandWaitTime, false, () -> leafLifeCycleAction(leaf));
    }

    private void leafLifeCycleAction(Leaf leaf) {
        leaf.transform().setVelocityY(LEAF_FALL_VELOCITY);
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
        Random treeAllocateRand = new Random(Objects.hash(x, GENERAL_SEED));
        return treeAllocateRand.nextInt(MAX_RANDOM_VALUE) <= TREE_DENSITY_PERCENTAGE;
        }


    private void windLeafActions(GameObject leaf){
        setRotateTransition(leaf);
        setScalingTransition(leaf);
    }

    private void setRotateTransition(GameObject leaf) {
        new Transition<>(
                leaf, // the game object being changed
                (angle) -> leaf.renderer().setRenderableAngle(angle), // the method to call
                (float) (-WIND_TRANSITION_RANGE * Math.PI), // initial transition value
                (float) (WIND_TRANSITION_RANGE * Math.PI), // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                LEAF_ROTATE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

    private void setScalingTransition(GameObject leaf) {
        new Transition<>(
                leaf, // the game object being changed
                (value) -> leaf.setDimensions(changeBounds(value, leaf.getDimensions().x())), // the method to call
                -LEAF_SCALE_DELTA, // initial transition value
                LEAF_SCALE_DELTA, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                LEAF_SCALE_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

    private Vector2 changeBounds(Float value, float x) {
        float size =  x + value;
        if (size < LEAF_MIN_SIZE){
            size = LEAF_MIN_SIZE;
        }
        if (size > LEAF_MAX_SIZE){
            size = LEAF_MAX_SIZE;
        }
        return new Vector2(size, size);
    }


}
