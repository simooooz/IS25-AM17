package it.polimi.ingsw.common.model.events;

import java.util.List;

public class EventContext {

    private static final ThreadLocal<EventCollector> collector = ThreadLocal.withInitial(EventCollector::new);

    public static void emit(GameEvent event) {
        event.emitTo(collector.get());
    }

    public static List<GameEvent> getAndClear() {
        List<GameEvent> events = collector.get().getEvents();
        collector.get().clear();
        return events;
    }

    public static void clear() {
        collector.get().clear();
    }

}