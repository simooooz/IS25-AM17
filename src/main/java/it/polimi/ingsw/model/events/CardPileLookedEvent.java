package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

/**
 * Event record representing a player looking at cards in a specific deck pile.
 * This event is triggered when a player is allowed to examine the contents of
 * a card deck or pile, typically as part of card selection mechanics or special abilities.
 * <p>
 * @param username  the username of the player who looked at the card pile
 * @param deckIndex the index identifying which deck was examined
 * @param cards     the list of cards that were revealed to the player
 * @author Generated Javadoc
 * @version 1.0
 */
public record CardPileLookedEvent(String username, Integer deckIndex, List<Card> cards) implements GameEvent {

    /**
     * Retrieves the message type identifier for this event.
     *
     * @return MessageType.CARD_PILE_LOOKED_EVENT indicating the specific event type
     */
    @Override
    public MessageType eventType() {
        return MessageType.CARD_PILE_LOOKED_EVENT;
    }

    /**
     * Retrieves the visibility scope for this event.
     * <p>
     * Only the player who performed the examination receives the event data.
     *
     * @return EventVisibility.PLAYER_ONLY indicating this event is private to the acting player
     */
    @Override
    public EventVisibility getVisibility() {
        return EventVisibility.PLAYER_ONLY;
    }

    /**
     * Retrieves the serialized arguments for network transmission.
     * <p>
     * @return array containing username, deck index, and serialized card list for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{username, deckIndex, CardFactory.serializeCardList(cards)};
    }

}
