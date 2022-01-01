package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class ScreenRendererManager {
    private int indexToFill;
    public LinkedList<ArrayList<Map.Entry<GameObject, Integer>>> gameObjectsList;
    private GameObjectCollection gameObjectCollection;
    private int screenBufferSize;

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
    public void setIndexToFill (int index) {
        indexToFill = index;
    }
    public void addGameObject(GameObject object, Integer layer){
        gameObjectsList.get(indexToFill).add(new AbstractMap.SimpleEntry<>(object, layer));
    }
    public void removeGameObjects(int index){
        removeArray(index);
        addEmptyArray(index);
    }

    private void removeArray(int index) {
        for (Map.Entry<GameObject, Integer> obj : gameObjectsList.get(index)){
            gameObjectCollection.removeGameObject(obj.getKey(), obj.getValue());
        }
        gameObjectsList.remove(index);
    }

    private void addEmptyArray(int index) {
        indexToFill = screenBufferSize - 1 - index;
        if (index == 0) {
            gameObjectsList.addLast(new ArrayList<>());
            return;
        }
        gameObjectsList.addFirst(new ArrayList<>());
    }


}
