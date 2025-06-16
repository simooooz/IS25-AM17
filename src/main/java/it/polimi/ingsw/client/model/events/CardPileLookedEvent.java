package it.polimi.ingsw.client.model.events;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

public record CardPileLookedEvent(String username, Integer deckIndex, List<ClientCard> cards) implements GameEvent {

    @Override
    public MessageType eventType() { return MessageType.CARD_PILE_LOOKED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return cards == null ? EventVisibility.OTHER_PLAYERS : EventVisibility.PLAYER_ONLY; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username, deckIndex, cards};
    }

}
