package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.GameEvent;

import java.util.List;

public interface ClientEventObserver {
    void onEvent(List<GameEvent> event);
}