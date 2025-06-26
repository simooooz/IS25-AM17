package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

public record CardPileReleasedEvent(String username) implements Event {

    @Override
    public MessageType eventType() { return MessageType.CARD_PILE_RELEASED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }

}
