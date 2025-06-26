package it.polimi.ingsw.common.model.events.lobby;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

public record JoinedLobbyEvent(String username) implements Event {

    @Override
    public MessageType eventType() {
        return MessageType.JOINED_LOBBY_EVENT;
    }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.OTHER_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }

}
