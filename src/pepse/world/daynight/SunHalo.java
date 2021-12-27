package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo extends GameObject {

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
    }

    private static void updateHalo(float deltaTime, GameObject sun, GameObject sunHalo){
        sunHalo.setCenter(sun.getCenter());
        // todo if time permits - change color to red

    }

    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color){
        Renderable sunImg = new OvalRenderable(color);
        Vector2 haloSize = sun.getDimensions().mult(1.5f);
        GameObject sunHalo = new SunHalo(Vector2.ZERO, haloSize, sunImg);
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.addComponent(deltaTime -> updateHalo(deltaTime, sun, sunHalo));
        return sunHalo;
    }





}
