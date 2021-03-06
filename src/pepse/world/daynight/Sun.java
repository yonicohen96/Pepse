package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class of sun object for pepse game
 */
public class Sun extends GameObject {

    private static final float SUN_SIZE = 100;
    private static final Vector2 SUN_DIMENSIONS = new Vector2(SUN_SIZE, SUN_SIZE);
    private static final float SUN_RATIO_X = 2;
    private static final float SUN_RATIO_Y = 6;

    private static final String SUN_TAG = "sun";
    private static final float SUN_INITIAL_MOVEMENT_TRANSITION = 0f;
    private static final double FINAL_MOVEMENT_TRANSITION_CYCLE = 2 * Math.PI;
    private static final double SUN_OVAL_RATIO = 1.8;
    public static final int MAX_COLOR_SCALE = 255;
    public static final int SUNSET_COLOR_SCALE = 126;

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

    /**
     * function to create an object of sun
     * @param gameObjects game object collection
     * @param layer the layer to insert the created object to
     * @param windowDimensions window's dimensions
     * @param cycleLength cycle length of sun
     * @return the created sun object
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength) {
        GameObject sun = createInstance(gameObjects, layer, windowDimensions);
        setMovementTransition(windowDimensions, cycleLength, sun);
        setColorTransition(cycleLength, sun);
        sun.setTag(SUN_TAG);
        return sun;
    }
/*
    function to set the changing sun color transition
 */
    private static void setColorTransition(float cycleLength, GameObject sun) {
        new Transition<>(
                sun, // the game object being changed
                (angle) -> Sun.setSunColor(sun, angle), // the method to call
                SUN_INITIAL_MOVEMENT_TRANSITION, // initial transition value
                (float) FINAL_MOVEMENT_TRANSITION_CYCLE, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use simple linear interpolator
                cycleLength, // transition sun
                Transition.TransitionType.TRANSITION_LOOP,
                null); // nothing further to execute upon reaching final value

    }

    private static void setSunColor(GameObject sun, float sunAngle) {
        sun.renderer().setRenderable(
                new OvalRenderable(new Color(MAX_COLOR_SCALE,(int)(SUNSET_COLOR_SCALE * Math.cos(sunAngle) +
                        SUNSET_COLOR_SCALE),0)));
    }

    /*
    function to create an instance of the sun
     */
    private static GameObject createInstance(GameObjectCollection gameObjects, int layer,
                                             Vector2 windowDimensions) {
        Renderable sunImg = new OvalRenderable(Color.YELLOW);
        Vector2 sunLocation = new Vector2(windowDimensions.x() / SUN_RATIO_X,
                windowDimensions.y() / SUN_RATIO_Y);
        GameObject sun = new Sun(sunLocation, SUN_DIMENSIONS, sunImg);
        gameObjects.addGameObject(sun, layer);
        return sun;
    }

    /*
    function to set the transition of the sun movement
     */
    private static void setMovementTransition(Vector2 windowDimensions, float cycleLength, GameObject sun) {
        new Transition<>(
                sun, // the game object being changed
                (angle) -> sun.setCenter(calcSunPosition(windowDimensions, angle)), // the method to call
                SUN_INITIAL_MOVEMENT_TRANSITION, // initial transition value
                (float) FINAL_MOVEMENT_TRANSITION_CYCLE, // final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT, // use simple linear interpolator
                cycleLength, // transition sun
                Transition.TransitionType.TRANSITION_LOOP,
                null); // nothing further to execute upon reaching final value
    }

    /*
    function to calculate position of sun
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        float radius = (windowDimensions.y() - SUN_DIMENSIONS.y()) / 2;
        float y = (float) ((windowDimensions.y() / 2) - radius * Math.cos(angleInSky));
        float x = (float) ((windowDimensions.x() / 2) - radius * SUN_OVAL_RATIO * Math.sin(angleInSky));
        return new Vector2(x, y);
    }

}
