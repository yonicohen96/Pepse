package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.ScreenRendererManager;

import java.awt.*;

public class StemProducer {
    private static final String STEM_BLOCK_TAG = "stem block";
    private static final Color STEM_COLOR = new Color(100, 50, 20);
    private final ScreenRendererManager rendererManager;
    private final int layer;

    public StemProducer(ScreenRendererManager screenRendererManager, int layer) {
        this.rendererManager = screenRendererManager;
        this.layer = layer;
    }

    /**
     * function to create a single tree's stem
     * @param x x coordinate to locate the stem
     * @param treeBaseY y coordinate of tree base
     * @param treeSize tree's size
     */
    public void createStem(int x, int treeBaseY, int treeSize) {
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
        rendererManager.addGameObject(gameObject, layer);
    }

}


