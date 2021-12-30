package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private static final Vector2 AVATAR_SIZE = new Vector2(60, 95);
    private static final float MOVEMENT_SPEED = 300;
    private static final float JUMP_SPEED = 300;
    private static final float INITIAL_ENERGY = 100;
    private static final float ACCELERATION_Y = 500;
    private static final float ENERGY_CHANGE = 0.5f;
    private static final String ANIMATION_PATH = "assets/trump/trump%s.png";
    private static final String STAND_IMG_PATH = "assets/trump/trump1.png";
    private final int ANIMATION_NUMBER = 3;
    private final float TIME_BETWEEN_CLIPS = 0.3f;
    private final AnimationRenderable avatarAnimationRenderables;
    private Renderable avatarStaticRenderable;
    private float energy;
    private UserInputListener inputListener;
    private ImageReader imageReader;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the obj ect. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.energy = INITIAL_ENERGY;
        this.transform().setAccelerationY(ACCELERATION_Y);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.physics().setMass(1);
        avatarStaticRenderable = imageReader.readImage(STAND_IMG_PATH, true);
        avatarAnimationRenderables = new AnimationRenderable(createAvatarRenderables(), TIME_BETWEEN_CLIPS);;
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
//        Renderable AvatarImg = imageReader.readImage(String.format(ANIMATION_PATH, "1"), true);
        Renderable AvatarImg = imageReader.readImage(STAND_IMG_PATH, true);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_SIZE, AvatarImg, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    private Renderable[] createAvatarRenderables() {
        Renderable[] renderables = new Renderable[ANIMATION_NUMBER];
        for (int i = 1; i < ANIMATION_NUMBER + 1; i++) {
            renderables[i - 1] = imageReader.readImage(String.format(ANIMATION_PATH, i), true);
        }
        return renderables;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        horizontalMovement();
        verticalMovement();
        if (getVelocity().x() > 0){
            renderer().setRenderable(avatarAnimationRenderables);
            renderer().setIsFlippedHorizontally(false);
        }
        else if (getVelocity().x() < 0){
            renderer().setRenderable(avatarAnimationRenderables);
            renderer().setIsFlippedHorizontally(true);
        }
        else{
            renderer().setRenderable(avatarStaticRenderable);
        }
        // todo delete prints
//        System.out.println(energy);

    }

    private void verticalMovement() {
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0){
                energy = Math.max(0, energy - ENERGY_CHANGE);
                this.transform().setVelocityY(-JUMP_SPEED);
                System.out.println(this.getVelocity().y());
            }
            else if (getVelocity().y() == 0){
                this.transform().setVelocityY(-JUMP_SPEED);
            }


//        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0){
//            setVelocity(getVelocity().add(Vector2.UP.mult(JUMP_SPEED)));
//            System.out.println(getVelocity().y());
        }
        else{
            if (this.getVelocity().y() == 0 && this.energy < INITIAL_ENERGY){
                energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);
            }
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
