package it.polimi.ingsw.common.model.events.lobby;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

public record CreatedLobbyEvent(String name, List<String> players, Boolean learnerMode, Integer maxPlayers) implements GameEvent {

    @Override
    public MessageType eventType() { return MessageType.CREATED_LOBBY_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.PLAYER_ONLY; }

    @Override
    public Object[] getArgs() {
        return new Object[]{name, players, learnerMode, maxPlayers};
    }

}
