package it.polimi.ingsw.common.model.events.lobby;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

public record LeftLobbyEvent(String username, List<String> playersNotify) implements Event {

    @Override
    public MessageType eventType() { return MessageType.LEFT_LOBBY_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.SPECIFIC_PLAYERS; }

    @Override
    public List<String> getTargetPlayers() {
        return playersNotify;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }

}
