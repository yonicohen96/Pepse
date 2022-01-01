package pepse.world.trees;


import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;


public class Leaf extends Block {
    private static final int LEAF_MASS = 1;
    private static final int LEAF_MOTION_AMPLITUDE = 12;
    private static final int LEAF_MOTION_FREQUENCY = 10;
    private final Vector2 originalPosition;


    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        this.originalPosition = topLeftCorner;
        physics().setMass(LEAF_MASS);
    }

    public void resetPosition(){
        this.setVelocity(Vector2.ZERO);
        this.setTopLeftCorner(this.originalPosition);
        this.renderer().setOpaqueness(1);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (this.getVelocity().y() > 0){
            this.transform().setTopLeftCornerX((float) (originalPosition.x() +
                    Math.sin(this.getCenter().y() / LEAF_MOTION_FREQUENCY) * LEAF_MOTION_AMPLITUDE));
        }
    }

}
