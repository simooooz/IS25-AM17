package it.polimi.ingsw.common.model.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collector responsible for gathering and managing system events.
 * Provides functionality for aggregating and distributing events
 * to their respective recipients.
 */
public class EventCollector {

    private final List<Event> regularEvents = new ArrayList<>();
    private final Map<String, Event> uniqueEvents = new HashMap<>();

    public void addRegularEvent(Event event) {
        regularEvents.add(event);
    }

    public void addUniqueEvent(UniqueEvent event) {
        uniqueEvents.put(event.getUniqueKey(), event);
    }

    public List<Event> getEvents() {
        List<Event> allEvents = new ArrayList<>();
        allEvents.addAll(regularEvents);
        allEvents.addAll(uniqueEvents.values());
        return allEvents;
    }

    public void clear() {
        regularEvents.clear();
        uniqueEvents.clear();
    }

}
