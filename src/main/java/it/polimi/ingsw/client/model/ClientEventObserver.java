package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.Event;

import java.util.List;

/**
 * Interface representing an observer for client-side events in the application.
 * Classes implementing this interface can be registered to receive notifications
 * about events dispatched by the {@code ClientEventBus}.
 * <p>
 * Implementers should define the event handling logic in the {@code onEvent} method,
 * which is invoked with a list of events whenever one or more events are published.
 * <p>
 * This interface serves as a fundamental component of the event-driven architecture
 * used on the client-side of the application.
 *
 * @see Event
 * @see ClientEventBus
 */
public interface ClientEventObserver {
    void onEvent(List<Event> event);
}