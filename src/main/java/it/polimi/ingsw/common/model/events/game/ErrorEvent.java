package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record ErrorEvent(String message) implements UniqueEvent {

    @Override
    public MessageType eventType() {
        return MessageType.ERROR;
    }

    @Override
    public EventVisibility getVisibility() {
        return EventVisibility.PLAYER_ONLY;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{message};
    }

}
