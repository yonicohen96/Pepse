package pepse.world.trees;

import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

public class Leaf extends Block {

    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        new Transition<Float>(
                this, // the game object being changed
                (angle) -> this.renderer().setRenderableAngle(angle), // the method to call
                (float) (-5 * Math.PI), // initial transition value
                (float) (5 * Math.PI), // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                1, // transition sun
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

}
