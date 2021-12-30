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
    }

    public void resetPosition(){
        this.setVelocity(Vector2.ZERO);
        this.setTopLeftCorner(this.originalPosition);
        this.renderer().setOpaqueness(1);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // todo check if to cha ge to states and to stop velocity if collided with ground
        if (this.getVelocity().y() > 0){
            this.transform().setTopLeftCornerX((float) (originalPosition.x() + Math.sin(this.getCenter().y() / 10) * 12));
        }
    }
}
