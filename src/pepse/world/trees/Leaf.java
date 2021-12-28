package pepse.world.trees;

import danogl.components.GameObjectPhysics;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

public class Leaf extends Block {
    private final Vector2 originalPosition;


    public Leaf(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        this.originalPosition = topLeftCorner;
        physics().setMass(1);
        // todo leaf should pass stem
    }

    public void resetPosition(){
        this.setVelocity(Vector2.ZERO);
        this.setTopLeftCorner(this.originalPosition);
        this.renderer().setOpaqueness(1);


    }


}
