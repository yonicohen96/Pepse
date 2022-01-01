package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun extends GameObject {

    private static final float SUN_SIZE = 100;
    private static final Vector2 SUN_DIMENSIONS = new Vector2(SUN_SIZE, SUN_SIZE);
    private static final float SUN_RATIO_X = 2;
    private static final float SUN_RATIO_Y = 6;

    private static final String SUN_TAG = "sun";
    private static final float SUN_INITIAL_TRANSITION = 0f;
    private static final double FINAL_TRANSITION_CYCLE = 2 * Math.PI;
    private static final double SUN_OVAL_RATIO = 1.8;


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
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

    }

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        GameObject sun = createInstance(gameObjects, layer, windowDimensions);
        setTransition(windowDimensions, cycleLength, sun);
        sun.setTag(SUN_TAG);
        return sun;

    }

    private static GameObject createInstance(GameObjectCollection gameObjects, int layer,
                                             Vector2 windowDimensions) {
        Renderable sunImg = new OvalRenderable(Color.YELLOW);
        Vector2 sunLocation = new Vector2(windowDimensions.x() / SUN_RATIO_X,
                windowDimensions.y() / SUN_RATIO_Y);
        GameObject sun = new Sun(sunLocation, SUN_DIMENSIONS, sunImg);
        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    private static void setTransition(Vector2 windowDimensions, float cycleLength, GameObject sun) {
        new Transition<>(
                sun, // the game object being changed
                (angle) -> sun.setCenter(calcSunPosition(windowDimensions, angle)), // the method to call
                SUN_INITIAL_TRANSITION, // initial transition value
                (float) FINAL_TRANSITION_CYCLE, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                cycleLength, // transition sun
                Transition.TransitionType.TRANSITION_LOOP,
                null); // nothing further to execute upon reaching final value
    }

    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        float radius = (windowDimensions.y() - SUN_DIMENSIONS.y()) / 2;
        float y = (float) ((windowDimensions.y() / 2) - radius * Math.cos(angleInSky));
        float x = (float) ((windowDimensions.x() / 2) - radius * SUN_OVAL_RATIO * Math.sin(angleInSky));
        return new Vector2(x, y);
    }


}
