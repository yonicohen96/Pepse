package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Controls the extended screen GameObjects, using the linkedList DS.
 */
public class ScreenRendererManager {
    private int indexToFill;
    private final LinkedList<ArrayList<Map.Entry<GameObject, Integer>>> gameObjectsList;
    private final GameObjectCollection gameObjectCollection;
    private final int screenBufferSize;

    /**
     * Constructor of the class, create an instance of the screenRendererManager using the given parameters
     * @param gameObjectCollection the gameObjectCollection of the game, holds all the
     *                             GameObjects of the game
     * @param gameObjectsList the DS that the class holds with the GameObjects of the each screen
     * @param screenBufferSize the number of screens we want to hold all their GameObjects parallel
     */
    public ScreenRendererManager(GameObjectCollection gameObjectCollection,
                                 LinkedList<ArrayList<Map.Entry<GameObject, Integer>>> gameObjectsList,
                                 int screenBufferSize) {
        this.gameObjectCollection = gameObjectCollection;
        this.screenBufferSize = screenBufferSize;
        this.indexToFill = 0;
        this.gameObjectsList = gameObjectsList;
        for (int i = 0; i < screenBufferSize; i++) {
            gameObjectsList.push(new ArrayList<Map.Entry<GameObject, Integer>>());
        }
    }

    /**
     * sets the index of the Node (in the linkList) that represents a collection of GameObjects
     * that we want to fill into
     * @param index the index of the screen we want to fill the GameObjects into.
     */
    public void setIndexToFill (int index) {
        indexToFill = index;
    }

    /**
     * adds a GameObject and its layer to a list (representing a screen) in the linkedList
     * (using the indexTOFIll)
     * @param object the GameObject to add
     * @param layer the layer of the gameObject to add
     */
    public void addGameObject(GameObject object, Integer layer){
        gameObjectsList.get(indexToFill).add(new AbstractMap.SimpleEntry<>(object, layer));
        gameObjectCollection.addGameObject(object, layer);

    }

    /**
     * remove all the gameObjects from a list in the linkedList using the given index argument
     * and add a new list to the array in the correct location to fill into later.
     * @param index the index of the screen to remove all the items from
     */
    public void removeGameObjects(int index){
        removeArray(index);
        addEmptyArray(index);
    }
/*
    A helper function that remove all the elements from the list in the given index in the linkList
 */
    private void removeArray(int index) {
        for (Map.Entry<GameObject, Integer> obj : gameObjectsList.get(index)){
            gameObjectCollection.removeGameObject(obj.getKey(), obj.getValue());
        }
        gameObjectsList.remove(index);
    }
/*
    adds an empty ListArray to the linkedList by the given index
 */
    private void addEmptyArray(int index) {
        indexToFill = screenBufferSize - 1 - index;
        if (index == 0) {
            gameObjectsList.addLast(new ArrayList<>());
            return;
        }
        gameObjectsList.addFirst(new ArrayList<>());
    }


}
