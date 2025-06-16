package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public record PlayersPositionUpdatedEvent(List<String> starting, List<SimpleEntry<String, Integer>> players) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.PLAYERS_POSITION_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{starting, players};
    }

}