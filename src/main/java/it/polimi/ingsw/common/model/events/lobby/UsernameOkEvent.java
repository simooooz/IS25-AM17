package it.polimi.ingsw.common.model.events.lobby;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

public record UsernameOkEvent(String username) implements Event {

    @Override
    public MessageType eventType() {
        return MessageType.USERNAME_OK_EVENT;
    }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.PLAYER_ONLY; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }

}
