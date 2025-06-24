package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

public record ShipBrokenEvent(String username, List<List<Integer>> parts) implements GameEvent {

    @Override
    public MessageType eventType() {
        return MessageType.SHIP_BROKEN_EVENT;
    }

    @Override
    public EventVisibility getVisibility() {
        return EventVisibility.SPECIFIC_PLAYERS;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{username, parts};
    }

    @Override
    public List<String> getTargetPlayers() {
        return List.of(username);
    }
}
