package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun extends GameObject{
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
        Vector2 sunSize = new Vector2(windowDimensions.y() / 6, windowDimensions.y() / 6);
        GameObject sun = new GameObject(sunLocation, sunSize, sunImg);
        gameObjects.addGameObject(sun, layer);
        return sun;

    }
}
