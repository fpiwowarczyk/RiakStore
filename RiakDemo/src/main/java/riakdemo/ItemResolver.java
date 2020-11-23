package riakdemo;

import com.basho.riak.client.api.cap.ConflictResolver;
import com.basho.riak.client.api.cap.UnresolvedConflictException;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ItemResolver implements ConflictResolver<Item> {
    @Override
    public Item resolve(List<Item> objectList) throws UnresolvedConflictException {
        Set<String> allObjects = new TreeSet<>();
        Set<String> allTombs = new TreeSet<>();
        for(Item object : objectList){
            String[] items = object.prodId.split(" ");
            allObjects.addAll(Arrays.asList(items));
            String[] tombs = object.tombstone.split(" ");
            allTombs.addAll(Arrays.asList(tombs));
        }
        allObjects.removeAll(allTombs);

        String[] allTombsArray = allTombs.toArray(new String[0]);
        String tombsResult = String.join(" ", allTombsArray);

        String[] allObjectsArray = allObjects.toArray(new String[0]);
        String objectsResult = String.join(" ", allObjectsArray);
        Item resolvedItem = new Item(objectList.get(0).userName,objectsResult,tombsResult);
        return resolvedItem;
    }
}
