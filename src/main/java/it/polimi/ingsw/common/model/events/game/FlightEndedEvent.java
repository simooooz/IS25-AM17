package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record FlightEndedEvent(String username) implements GameEvent {

    @Override
    public MessageType eventType() {
        return MessageType.FLIGHT_ENDED_EVENT;
    }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }
}
