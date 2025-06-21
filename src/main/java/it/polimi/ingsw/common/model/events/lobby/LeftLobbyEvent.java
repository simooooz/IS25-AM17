package it.polimi.ingsw.common.model.events.lobby;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record LeftLobbyEvent(String username, List<String> playersNotify) implements GameEvent {

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
