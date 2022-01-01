package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Class of the block GameObject, uses to create different object in the game (leaf, terrain, tree stem)
 */
public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * constructor of the class, create instance of Block using the given parameters
     * @param topLeftCorner the initial position of the Block
     * @param renderable the image of the block to render on the screen
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * round the given number into number that divided by the Block size
     * @param minX the number to round
     * @return the block size rounded number
     */
    public static int roundToBlock(int minX){
        if (minX >= 0){
            return (minX / Block.SIZE) * Block.SIZE;
        }
        return ((minX / Block.SIZE) - 1) * Block.SIZE;
    }

}
