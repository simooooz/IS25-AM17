package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

public record CardPileLookedEvent(String username, Integer deckIndex, List<Card> cards) implements GameEvent {

    @Override
    public MessageType eventType() { return MessageType.CARD_PILE_LOOKED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.PLAYER_ONLY; }

    @Override
    public Object[] getArgs() {
        return new Object[]{username, deckIndex, CardFactory.serializeCardList(cards)};
    }

}
