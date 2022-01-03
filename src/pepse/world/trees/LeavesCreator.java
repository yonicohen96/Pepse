package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.ScreenRendererManager;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class LeavesCreator {
    private static final Color LEAVES_COLOR = new Color(50, 200, 30);
    private static final float LEAF_SCALE_DELTA = 0.5f;
    private static final float FADEOUT_TIME = 10f;
    private static final int LEAF_DEATH_RANGE = 5;
    private static final int LEAF_LIVE_RANGE = 300;
    private static final int LEAF_FALL_RANGE = 50;
    private static final float LEAF_RANK_RATIO = 5f;
    private static final int LEAF_FALL_VELOCITY = 50;
    private static final int WIND_TRANSITION_RANGE = 5;
    private static final float LEAF_ROTATE_TRANSITION_TIME = 1.1f;
    private static final float LEAF_SCALE_TRANSITION_TIME = 0.8f;
    private static final int LEAF_MIN_SIZE = 27;
    private static final int LEAF_MAX_SIZE = 40;
    private static final int MAX_LEAVES_CREATION_RANK = 100;
    private static final int LEAVES_CREATION_RANK = 80;
    private static final String LEAF_TAG = "leaf";
    private final Random random;
    private final ScreenRendererManager rendererManager;
    private final int layer;
    private final int seed;

    public LeavesCreator(int seed, ScreenRendererManager screenRendererManager, int layer) {
        this.seed = seed;
        this.random = new Random(seed);
        this.rendererManager = screenRendererManager;
        this.layer = layer;
    }

    /**
     * function to create leaves of a single tree given location treetop and size
     * @param x location of tree
     * @param treetop treetop y coordinate
     * @param treeSize size of tree
     */
    public void createLeaves(int x, int treetop, int treeSize) {
        Random leavesRandom = new Random(Objects.hash(x * seed));
        int delta = Block.roundToBlock((treeSize * Block.SIZE / 3));
        for (int i = x - delta ; i <= x + delta; i+=Block.SIZE) {
            for (int j = treetop - delta; j <= treetop + delta ; j+=Block.SIZE) {
                if (leavesRandom.nextInt(MAX_LEAVES_CREATION_RANK) < LEAVES_CREATION_RANK){
                    createLeaf(i, j);
                }
            }
        }
    }

    /*
    function to create a single leaf
     */
    private void createLeaf(int blockX, int blockY) {
        Renderable renderable = new RectangleRenderable(LEAVES_COLOR);
        Leaf leaf = new Leaf(new Vector2(blockX, blockY), renderable);
        startLifeCycle(leaf);
        float windRandWaitTime = (random.nextInt(LEAF_FALL_RANGE)/ LEAF_RANK_RATIO);
        new ScheduledTask(leaf, windRandWaitTime, false
                , () -> windLeafActions(leaf));
        leaf.setTag(LEAF_TAG);
        rendererManager.addGameObject(leaf, layer);
    }

    /*
    function to start a life cycle of a leaf
     */
    private void startLifeCycle(Leaf leaf) {
        leaf.resetPosition();
        float lifeRandWaitTime = (random.nextInt(LEAF_LIVE_RANGE)/ LEAF_RANK_RATIO);
        new ScheduledTask(leaf, lifeRandWaitTime, false, () -> leafLifeCycleAction(leaf));
    }

    /*
    a function to perform leaf's life cycle's actions
     */
    private void leafLifeCycleAction(Leaf leaf) {
        leaf.transform().setVelocityY(LEAF_FALL_VELOCITY);
        leaf.renderer().fadeOut(FADEOUT_TIME, () -> postFadeActions(leaf));
    }

    /*
    post fade actions of a leaf
     */
    private void postFadeActions(Leaf leaf) {
        float deathTime = (float) random.nextInt(LEAF_DEATH_RANGE);
        new ScheduledTask(leaf, deathTime, false, () -> startLifeCycle(leaf));
    }

    /*
    leaf actions - rotation and scaling
     */
    private void windLeafActions(GameObject leaf){
        setRotateTransition(leaf);
        setScalingTransition(leaf);
    }

    /*
    function to set rotation transition for a leaf
     */
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

    /*
    function to set scaling transition for a leaf
     */
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

    /*
    a function to change leaf's bounds
     */
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
