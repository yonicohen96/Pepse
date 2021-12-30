package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private static final Vector2 AVATAR_SIZE = new Vector2(30, 70);
    private static final float MOVEMENT_SPEED = 300;
    private static final float JUMP_SPEED = 300;
    private static final float ACCELERATION_Y = 20;
    private UserInputListener inputListener;
    private ImageReader imageReader;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.transform().setAccelerationY(ACCELERATION_Y);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Renderable AvatarImg = new RectangleRenderable(Color.BLUE); // todo change to animation
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_SIZE, AvatarImg, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        horizontalMovement();
        jump();

    }

    private void jump() {
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
            setVelocity(getVelocity().add(Vector2.UP.mult(JUMP_SPEED)));
            System.out.println(getVelocity().y());
        }
    }

    private void horizontalMovement() {
        Vector2 movementDir = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)){
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            movementDir = movementDir.add(Vector2.RIGHT);
        }
            setVelocity(movementDir.mult(MOVEMENT_SPEED).add(new Vector2(0, getVelocity().y())));
    }
}
