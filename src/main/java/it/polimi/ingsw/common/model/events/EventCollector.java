package it.polimi.ingsw.common.model.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventCollector {

    private final List<GameEvent> regularEvents = new ArrayList<>();
    private final Map<String, GameEvent> uniqueEvents = new HashMap<>();

    public void addRegularEvent(GameEvent event) {
        regularEvents.add(event);
    }

    public void addUniqueEvent(UniqueEvent event) {
        uniqueEvents.put(event.getUniqueKey(), event);
    }

    public List<GameEvent> getEvents() {
        List<GameEvent> allEvents = new ArrayList<>();
        allEvents.addAll(regularEvents);
        allEvents.addAll(uniqueEvents.values());
        return allEvents;
    }

    public void clear() {
        regularEvents.clear();
        uniqueEvents.clear();
    }

}
