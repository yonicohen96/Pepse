package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * class of night object for the fame
 */
public class Night{
    private static final String NIGHT_TAG = "night";
    private static final Float MIDNIGHT_OPACITY = 0.7f;
    private static final Float INITIAL_TRANSITION_VALUE = 0f;

    /**
     * function to create night object
     * @param gameObjects the game collection object
     * @param layer layer to insert new object
     * @param windowDimensions window's dimensions
     * @param cycleLength cycle length of night
     * @return the created night object
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength){
        GameObject nightBlock = createInstance(gameObjects, layer, windowDimensions);
        nightBlock.setTag(NIGHT_TAG);
        setTransition(cycleLength, nightBlock);
        return nightBlock;
    }

    /*
    function to create an instance for night
     */
    private static GameObject createInstance(GameObjectCollection gameObjects, int layer,
                                             Vector2 windowDimensions) {
        Renderable nightBlockImg = new RectangleRenderable(Color.BLACK);
        GameObject nightBlock = new GameObject(Vector2.ZERO, windowDimensions, nightBlockImg);
        nightBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(nightBlock, layer);
        return nightBlock;
    }

    /*
    function to set transition for night
     */
    private static void setTransition(float cycleLength, GameObject nightBlock) {
        new Transition<>(
                nightBlock, // the game object being changed
                nightBlock.renderer()::setOpaqueness, // the method to call
                INITIAL_TRANSITION_VALUE, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT, // use a cubic interpolator
                cycleLength, // transition fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null); // nothing further to execute upon reaching final value
    }

}
