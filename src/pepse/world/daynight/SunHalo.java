package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class that represents a sun halo object for pepse game
 */
public class SunHalo extends GameObject {
    private static final float SUN_HALO_RATIO = 1.5f;
    private static final String SUN_HALO_TAG = "sunHalo";

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public SunHalo(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /*
    function to update halo
     */
    private static void updateHalo(float deltaTime, GameObject sun, GameObject sunHalo){
        sunHalo.setCenter(sun.getCenter());
    }

    /**
     * method to create an object of sun halo
     * @param gameObjects game object collection
     * @param layer the layer of the created object
     * @param sun the sun object
     * @param color the color of the halo
     * @return the created object
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        GameObject sunHalo = createInstance(gameObjects, layer, sun, color);
        sunHalo.addComponent(deltaTime -> updateHalo(deltaTime, sun, sunHalo));
        sun.setTag(SUN_HALO_TAG);
        return sunHalo;

    }

    /*
    function to create an instance for the sun halo
     */
    private static GameObject createInstance(GameObjectCollection gameObjects, int layer, GameObject sun,
                                             Color color) {
        Renderable sunImg = new OvalRenderable(color);
        Vector2 haloSize = sun.getDimensions().mult(SUN_HALO_RATIO);
        GameObject sunHalo = new SunHalo(Vector2.ZERO, haloSize, sunImg);
        gameObjects.addGameObject(sunHalo, layer);
        return sunHalo;
    }

}
