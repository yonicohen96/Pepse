package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {
    private static enum AvatarStatesNames {FLY , WALK, STAND}
    private static final Vector2 AVATAR_GROUND_SIZE = new Vector2(60, 95);
    private static final Vector2 AVATAR_FLY_SIZE = new Vector2(140, 140);
    private static final float MOVEMENT_SPEED = 300;
    private static final float JUMP_SPEED = 300;
    private static final float FLIGHT_SPEED = 300;
    private static final float INITIAL_ENERGY = 100;
    private static final float ACCELERATION_Y = 500;
    private static final float ENERGY_CHANGE = 0.5f;
    private static final String WALK_ANIMATION_PATH = "assets/trump/trump%s.png";
    private static final String STAND_IMG_PATH = "assets/trump/trump1.png";
    private static final int FLY_ANIMATION_NUMBER = 11;
    private static final String FLY_ANIMATION_PATH = "assets/flyingTrump/flyingTrump%s.jpg";
    private final int WALK_ANIMATION_NUMBER = 3;
    private final float TIME_BETWEEN_CLIPS = 0.3f;
    private final AnimationRenderable avatarWalkAnimationRenderables;
    private final AnimationRenderable avatarFlyAnimationRenderables;
    private Renderable avatarStandRenderable;
    private float energy;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private AvatarState curState;
    private final AvatarState flyState;
    private final AvatarState walkState;
    private final AvatarState standState;


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
        avatarStandRenderable = imageReader.readImage(STAND_IMG_PATH, true);
        avatarWalkAnimationRenderables = new AnimationRenderable(createWalkAvatarRenderables(), TIME_BETWEEN_CLIPS);
        avatarFlyAnimationRenderables = new AnimationRenderable(createFlyAvatarRenderables(), TIME_BETWEEN_CLIPS/10);
        this.flyState = new AvatarState(this::flyUpdate, avatarFlyAnimationRenderables, AVATAR_FLY_SIZE,
                AvatarStatesNames.FLY);
        this.walkState = new AvatarState(this::walkUpdate, avatarWalkAnimationRenderables, AVATAR_GROUND_SIZE,
                AvatarStatesNames.WALK);
        this.standState = new AvatarState(this::standUpdate, avatarStandRenderable, AVATAR_GROUND_SIZE,
                AvatarStatesNames.STAND);
        this.curState = this.standState;
    }

    private Renderable[] createFlyAvatarRenderables() {
        Renderable [] renderables = new Renderable[FLY_ANIMATION_NUMBER];
        for (int i = 1; i < FLY_ANIMATION_NUMBER + 1; i++) {
            renderables[i - 1] = imageReader.readImage(String.format(FLY_ANIMATION_PATH, i), true);
        }
        return renderables;
    }

    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
//        Renderable AvatarImg = imageReader.readImage(String.format(ANIMATION_PATH, "1"), true);
        Renderable AvatarImg = imageReader.readImage(STAND_IMG_PATH, true);
        Avatar avatar = new Avatar(topLeftCorner, AVATAR_GROUND_SIZE, AvatarImg, inputListener, imageReader);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }

    private Renderable[] createWalkAvatarRenderables() {
        Renderable[] renderables = new Renderable[WALK_ANIMATION_NUMBER];
        for (int i = 1; i < WALK_ANIMATION_NUMBER + 1; i++) {
            renderables[i - 1] = imageReader.readImage(String.format(WALK_ANIMATION_PATH, i), true);
        }
        return renderables;
    }

//    @Override
//    public void update2(float deltaTime) {
//        super.update(deltaTime);
//        horizontalMovement();
//        verticalMovement();
//
//        if (getVelocity().x() > 0){
//            renderer().setIsFlippedHorizontally(false);
//        }
//        else if (getVelocity().x() < 0){
//            renderer().setIsFlippedHorizontally(true);
//        }
//        else if (getVelocity().equals(Vector2.ZERO)){
//            // standing
//            this.stand();
//
//        }
//    }



    private void verticalMovement() {
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0){
                // flying
                energy = Math.max(0, energy - ENERGY_CHANGE);
                this.transform().setVelocityY(-JUMP_SPEED);
//                this.fly();
            }
            else {
                //this.walk();
                if (getVelocity().y() == 0) {
                    // jumping
                    this.transform().setVelocityY(-JUMP_SPEED);
                }
            }
            return;
        }
        // falling / on the ground
        if (this.getVelocity().y() == 0 && this.energy < INITIAL_ENERGY){
            energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);

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

//        if(getVelocity().x() != 0){
//            this.walk();
//        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // todo - delta this comment  from here - new code


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        horizontalMovement();
        AvatarState newState = checkState();

        // update state if needed
        if (curState != newState) {
            curState = newState;
            curState.setState();
        }
        curState.update();
//
//        verticalMovement();
//
//        if (getVelocity().x() > 0){
//            renderer().setIsFlippedHorizontally(false);
//        }
//        else if (getVelocity().x() < 0){
//            renderer().setIsFlippedHorizontally(true);
//        }
//        else if (getVelocity().equals(Vector2.ZERO)){
//            // standing
//            this.stand();

        }

    private AvatarState checkState() {
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0){
                //this.transform().setVelocityY(-JUMP_SPEED);
                return flyState;
            }
            else {

//                return AvatarStatesNames.WALK;
                if (getVelocity().y() == 0) {
                     //jumping
                    this.transform().setVelocityY(-JUMP_SPEED);
                }
            }
//            return null;
        }
        // falling / on the ground
        if (this.getVelocity().y() == 0 && this.energy <= INITIAL_ENERGY){
            if(getVelocity().x() ==0){
                return standState;
            }

//            energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);

        }
        return walkState;
    }



    private void checkChangeDirection(){
        if (getVelocity().x() > 0){
            renderer().setIsFlippedHorizontally(false);
        }
        else if (getVelocity().x() < 0){
            renderer().setIsFlippedHorizontally(true);
        }
    }


    // todo make sure that in updates - changes x directions

    private void flyUpdate(){
        energy = Math.max(0, energy - ENERGY_CHANGE);
        transform().setVelocityY(-FLIGHT_SPEED);
        checkChangeDirection();
    }

    private void walkUpdate(){
        if (getVelocity().y() <= 0){
            energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);
        }
        checkChangeDirection();
    }

    private void standUpdate(){
        if (getVelocity().y() <= 0){
            energy = Math.min(INITIAL_ENERGY, energy + ENERGY_CHANGE);
        }
    }


    private class AvatarState{


        private Runnable updateOperations;
        private Renderable renderable;
        private Vector2 dimensions;
        private AvatarStatesNames stateName;

        public AvatarState(Runnable updateOperations, Renderable renderable, Vector2 dimensions,
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
