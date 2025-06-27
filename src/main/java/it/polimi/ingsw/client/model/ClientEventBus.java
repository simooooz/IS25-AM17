package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The ClientEventBus is a singleton class responsible for managing the
 * dispatch and subscription of client-side events. It supports event batching
 * to optimize event handling and allows observers to subscribe to receive
 * notifications of published events.
 * <p>
 * The class maintains a list of subscribed observers and manages events
 * through batching or immediate publication.
 */
public class ClientEventBus {

    private boolean inBatch = false;
    private boolean hasPendingChanges = false;
    private final List<Event> events = new ArrayList<>();

    private final List<ClientEventObserver> observers = new CopyOnWriteArrayList<>();
    private static ClientEventBus instance;

    public static ClientEventBus getInstance() {
        if (instance == null) {
            instance = new ClientEventBus();
        }
        return instance;
    }

    public void subscribe(ClientEventObserver observer) {
        observers.add(observer);
    }

    public void startBatch() {
        events.clear();
        inBatch = true;
        hasPendingChanges = false;
    }

    public void endBatch() {
        inBatch = false;

        if (hasPendingChanges) {
            publishAll();
            hasPendingChanges = false;
        }
    }

    public void publish(Event event) {
        events.add(event);

        if (inBatch)
            hasPendingChanges = true;
        else {
            for (ClientEventObserver observer : observers)
                observer.onEvent(events);
            events.clear();
        }
    }

    public void publishAll() {
        for (ClientEventObserver observer : observers)
            observer.onEvent(events);
        events.clear();
    }

}