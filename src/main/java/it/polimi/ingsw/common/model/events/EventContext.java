package it.polimi.ingsw.common.model.events;

import java.util.List;

public class EventContext {

    private static final ThreadLocal<EventCollector> collector = ThreadLocal.withInitial(EventCollector::new);

    public static void emit(Event event) {
        event.emitTo(collector.get());
    }

    public static List<Event> getAndClear() {
        List<Event> events = collector.get().getEvents();
        collector.get().clear();
        return events;
    }

    public static void clear() {
        collector.get().clear();
    }

}