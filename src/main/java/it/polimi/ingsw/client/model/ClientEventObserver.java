package it.polimi.ingsw.client.model;

import it.polimi.ingsw.common.model.events.GameEvent;

public interface ClientEventObserver {
    void onEvent(GameEvent event);
}