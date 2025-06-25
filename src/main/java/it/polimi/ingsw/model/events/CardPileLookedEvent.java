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
 * The event captures important information about deck inspection:
 * - Which player performed the action
 * - Which deck was examined (by index)
 * - What cards were revealed to the player
 * <p>
 * This is a player-private event, meaning only the player who looked at the pile
 * receives information about the cards they saw. This maintains game balance by
 * preventing information leakage about deck contents to other players.
 * <p>
 * Common use cases include:
 * - Looking at the top cards of adventure decks before drawing
 * - Examining available cards during selection phases
 * - Special card abilities that allow deck inspection
 * - Preview mechanics for strategic decision making
 *
 * @param username  the username of the player who looked at the card pile
 * @param deckIndex the index identifying which deck was examined
 * @param cards     the list of cards that were revealed to the player
 * @author Generated Javadoc
 * @version 1.0
 */
public record CardPileLookedEvent(String username, Integer deckIndex, List<Card> cards) implements GameEvent {

    /**
     * Retrieves the message type identifier for this event.
     * <p>
     * This identifier is used by the network messaging system to properly
     * route and handle the event when transmitted between client and server.
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
     * Card pile examination events are private to maintain game balance and
     * prevent information about deck contents from being revealed to other players.
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
     * Converts the event data into a format suitable for network communication:
     * - Username is passed directly as a string identifier
     * - Deck index is passed as an integer identifier
     * - Card list is serialized using CardFactory for proper network transmission
     * <p>
     * The serialization ensures that card data is properly formatted for
     * client-server communication while maintaining all necessary information
     * about the revealed cards.
     *
     * @return array containing username, deck index, and serialized card list for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{username, deckIndex, CardFactory.serializeCardList(cards)};
    }

}
