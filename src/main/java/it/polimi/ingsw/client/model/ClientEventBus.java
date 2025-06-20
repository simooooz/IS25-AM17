package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.BatchEndedEvent;
import it.polimi.ingsw.common.model.events.GameEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientEventBus {

    private boolean inBatch = false;
    private boolean hasPendingChanges = false;

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
        inBatch = true;
        hasPendingChanges = false;
    }

    public void endBatch() {
        inBatch = false;

        if (hasPendingChanges) {
            publish(new BatchEndedEvent());
            hasPendingChanges = false;
        }
    }

    public void publish(GameEvent event) {
        if (inBatch)
            hasPendingChanges = true;
        else
            for (ClientEventObserver observer : observers)
                observer.onEvent(event);
    }

}