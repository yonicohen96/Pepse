package pepse.world.trees;

import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

public class Leaf extends Block {

    private static final float LEAF_SCALE_DELTA = 0.5f;

    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        new Transition<Float>(
                this, // the game object being changed
                (angle) -> this.renderer().setRenderableAngle(angle), // the method to call
                (float) (-5 * Math.PI), // initial transition value
                (float) (5 * Math.PI), // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                1.1f, // transition sun
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        new Transition<Float>(
                this, // the game object being changed
                (value) -> this.setDimensions(changeBounds(value)), // the method to call
                -LEAF_SCALE_DELTA, // initial transition value
                LEAF_SCALE_DELTA, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                0.8f, // transition sun
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

    private Vector2 changeBounds(Float value) {
        float size = this.getDimensions().x() + value;
        if (size < 20){
            size = 20;
        }
        if (size > 40){
            size = 40;
        }
        return new Vector2(size, size);
    }

}
