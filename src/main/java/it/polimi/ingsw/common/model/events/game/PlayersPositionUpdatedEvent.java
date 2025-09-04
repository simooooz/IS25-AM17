package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

public record PlayersPositionUpdatedEvent(List<String> starting, List<SimpleEntry<String, Integer>> players) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.PLAYERS_POSITION_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        List<SimpleEntry<String, Integer>> playersCopy = players.stream()
            .map(SimpleEntry::new)
            .toList();
        return new Object[]{ new ArrayList<>(starting), playersCopy };
    }

}