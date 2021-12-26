package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.beans.Transient;

public class Sun extends GameObject{
    // TODO check if should change according to window dims
    private static final Vector2 SUN_SIZE = new Vector2(100, 100);


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Sun(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);

    }

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        Renderable sunImg = new OvalRenderable(Color.YELLOW);
        Vector2 sunLocation = new Vector2(windowDimensions.x() / 2, windowDimensions.y() / 6);
        GameObject sun = new Sun(sunLocation, SUN_SIZE, sunImg);
        gameObjects.addGameObject(sun, layer);
        new Transition<Float>(
                sun, // the game object being changed
                (angle) -> sun.setCenter(calcSunPosition(windowDimensions, angle)), // the method to call
                0f, // initial transition value
                (float) (2 * Math.PI), // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                cycleLength, // transition sun
                Transition.TransitionType.TRANSITION_LOOP,
                null); // nothing further to execute upon reaching final value
        return sun;

    }

    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angle) {
        float radius = (windowDimensions.y() - SUN_SIZE.y()) / 2;
        float y = (float) ((windowDimensions.y() / 2) - radius * Math.cos(angle));
        float x = (float) ((windowDimensions.x() / 2) - radius * Math.sin(angle));
        return new Vector2(x, y);
    }


}
