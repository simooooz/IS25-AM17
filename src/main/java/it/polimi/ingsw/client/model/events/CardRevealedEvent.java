package it.polimi.ingsw.client.model.events;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record CardRevealedEvent(ClientCard card) implements GameEvent {

    @Override
    public MessageType eventType() { return MessageType.CARD_REVEALED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{card};
    }

}