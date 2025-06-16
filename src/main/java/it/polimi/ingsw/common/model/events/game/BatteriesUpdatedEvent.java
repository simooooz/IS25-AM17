package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record BatteriesUpdatedEvent(Integer id, Integer batteries) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.BATTERIES_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{id, batteries};
    }

    @Override
    public String getUniqueKey() {
        return eventType().toString().concat("_").concat(String.valueOf(id));
    }

}
