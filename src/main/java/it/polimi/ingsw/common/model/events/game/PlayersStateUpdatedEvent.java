package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.Map;

public record PlayersStateUpdatedEvent(Map<String, PlayerState> states) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.PLAYERS_STATE_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{states};
    }

}
