package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.Event;

import java.util.List;

public interface ClientEventObserver {
    void onEvent(List<Event> event);
}