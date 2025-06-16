package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record CrewUpdatedEvent(Integer id, Integer humans, AlienType alien) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.CREW_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{id, humans, alien};
    }

    @Override
    public String getUniqueKey() {
        return eventType().toString().concat("_").concat(String.valueOf(id));
    }

}
