package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {
    private static final String NIGHT_TAG = "night";
    private static final Float MIDNIGHT_OPACITY = 0.7f;
    private static final Float INITIAL_TRANSITION_VALUE = 0f;

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        Renderable nightBlockImg = new RectangleRenderable(Color.BLACK);
        GameObject nightBlock = new GameObject(Vector2.ZERO, windowDimensions, nightBlockImg);
        nightBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(nightBlock, layer);
        nightBlock.setTag(NIGHT_TAG);
        new Transition<Float>(
                nightBlock, // the game object being changed
                nightBlock.renderer()::setOpaqueness, // the method to call
                INITIAL_TRANSITION_VALUE, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength / 2, // transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
        return nightBlock;
    }
}
