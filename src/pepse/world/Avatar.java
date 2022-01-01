package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    public static final int AVATAR_MASS = 1;

    private enum AvatarStatesNames {FLY, FALL, WALK, STAND}
    private static final Vector2 AVATAR_SIZE = new Vector2(60, 95);
    private static final Vector2 AVATAR_FLY_SIZE = new Vector2(140, 140);
    private static final float MOVEMENT_SPEED = 300;
    private static final float JUMP_SPEED = 300;
    private static final float FLIGHT_SPEED = 300;
    private static final float INITIAL_ENERGY = 100;
    private static final float ACCELERATION_Y = 500;
    private static final float ENERGY_CHANGE = 0.5f;
    private static final String WALK_ANIMATION_PATH = "assets/trump/trump%s.png";
    private static final String STAND_IMG_PATH = "assets/trump/trump1.png";
//    private static final String SOUND_BULLSHIT_READER = "assets/trumpsound/BULLSHIT.mp3";
//    private static final String SOUND_WIN_READER = "assets/trumpsound/WIN.mp3";
//    private static final String SOUND_SMART_READER = "assets/trumpsound/SMART.mp3";
//    private static final String SOUND_RICH_READER = "assets/trumpsound/RICH.mp3";
    private static final int FLY_ANIMATION_NUMBER = 11;
    private static final String FLY_ANIMATION_PATH = "assets/flyingTrump/flyingTrump%s.jpg";
    private final int WALK_ANIMATION_NUMBER = 3;
    private final float TIME_BETWEEN_CLIPS = 0.3f;
    private float energy;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private AvatarState curState;
    private final AvatarState flyState;
    private final AvatarState fallState;
    private final AvatarState walkState;
    private final AvatarState standState;
    private boolean collide = true;


    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        collide = true;
    }


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
        Renderable avatarStandRenderable = imageReader.readImage(STAND_IMG_PATH, true);
        AnimationRenderable avatarWalkAnimationRenderables = new AnimationRenderable(createWalkAvatarRenderables(), TIME_BETWEEN_CLIPS / WALK_ANIMATION_NUMBER);
        AnimationRenderable avatarFlyAnimationRenderables = new AnimationRenderable(createFlyAvatarRenderables(),
                TIME_BETWEEN_CLIPS / FLY_ANIMATION_NUMBER);
        setAvatarProperties();
        this.flyState = new AvatarState(this::flyUpdate, avatarFlyAnimationRenderables, AVATAR_FLY_SIZE,
                AvatarStatesNames.FLY);
        this.fallState = new AvatarState(this::fallUpdate, avatarWalkAnimationRenderables, AVATAR_SIZE,
                AvatarStatesNames.FALL);
        this.walkState = new AvatarState(this::walkUpdate, avatarWalkAnimationRenderables, AVATAR_SIZE,
                AvatarStatesNames.WALK);
        this.standState = new AvatarState(this::standUpdate, avatarStandRenderable, AVATAR_SIZE,
                AvatarStatesNames.STAND);
        this.curState = this.standState;
    }



    private void setAvatarProperties() {
        this.transform().setAccelerationY(ACCELERATION_Y);
        this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.physics().setMass(AVATAR_MASS);
    }

    private Renderable[] createFlyAvatarRenderables() {
        Renderable [] renderables = new Renderable[FLY_ANIMATION_NUMBER];
        for (int i = 1; i < FLY_ANIMATION_NUMBER + 1; i++) {
            renderables[i - 1] = imageReader.readImage(String.format(FLY_ANIMATION_PATH, i),
                    true);
        }
        return renderables;
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Renderable AvatarImg = imageReader.readImage(STAND_IMG_PATH, true);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_SIZE, AvatarImg, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    private Renderable[] createWalkAvatarRenderables() {
        Renderable[] renderables = new Renderable[WALK_ANIMATION_NUMBER];
        for (int i = 1; i < WALK_ANIMATION_NUMBER + 1; i++) {
            renderables[i - 1] = imageReader.readImage(String.format(WALK_ANIMATION_PATH, i),
                    true);
        }
        return renderables;
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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        horizontalMovement();
        AvatarState newState = checkState();
        // update state if needed
        changeState(newState);
        // jumping
        jump();
        curState.update();

        }

    private void jump() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && curState.stateName !=
                AvatarStatesNames.FLY && getVelocity().y() == 0){
            // check if can jump while reaching top of flight
            this.transform().setVelocityY(-JUMP_SPEED);
        }
    }

    private void changeState(AvatarState newState) {
        if (curState != newState) {
            curState = newState;
            curState.setState();
        }
    }

    private AvatarState checkState() {
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && energy > 0) {
            return flyState;
        }
        if (curState.stateName == AvatarStatesNames.FLY || curState.stateName == AvatarStatesNames.FALL
                && !collide){
            return fallState;
        }
        if (getVelocity().x() != 0) {
            return walkState;
        }
        return standState;
    }


    private void checkChangeDirection(){
        if (getVelocity().x() > 0){
            renderer().setIsFlippedHorizontally(false);
        }
        else if (getVelocity().x() < 0){
            renderer().setIsFlippedHorizontally(true);
        }
    }

    private void flyUpdate(){
        energy = Math.max(0, energy - ENERGY_CHANGE);
        transform().setVelocityY(-FLIGHT_SPEED);
        checkChangeDirection();
        collide = false;
    }

    private void fallUpdate(){
        checkChangeDirection();
    }

    private void walkUpdate(){
        energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);
        checkChangeDirection();
    }

    private void standUpdate(){
        energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);
    }


    private class AvatarState{
        private final Runnable updateOperations;
        private final Renderable renderable;
        private final Vector2 dimensions;
        private final AvatarStatesNames stateName;

        private AvatarState(Runnable updateOperations, Renderable renderable, Vector2 dimensions,
                           AvatarStatesNames state) {
            this.updateOperations = updateOperations;
            this.renderable = renderable;
            this.dimensions = dimensions;
            this.stateName = state;
        }

        private void update(){
            updateOperations.run();
        }

        private void setState(){
            curState = this;
            renderer().setRenderable(renderable);
            setDimensions(dimensions);
        }

    }
}
