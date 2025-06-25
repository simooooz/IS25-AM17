package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event record representing the public revelation of a card to all players.
 * This event is triggered when a card is drawn, activated, or otherwise revealed
 * during normal game flow where all players need to see the same card information.
 * <p>
 * Card revelation is a fundamental game mechanic that occurs in various situations:
 * - Drawing the next adventure card that all players will encounter
 * - Revealing the active card that defines the current challenge or opportunity
 * - Exposing cards during certain game phases where transparency is required
 * - Showing cards that affect all players simultaneously
 * <p>
 * Unlike private card examination events, card revelations are public information
 * that all players receive simultaneously. This ensures that everyone has the same
 * knowledge about the active game state and can make informed decisions based on
 * the revealed card's effects and requirements.
 * <p>
 * The event provides the complete card information to all players, allowing them
 * to understand the upcoming challenges, opportunities, or effects that the card
 * will impose on the game state.
 *
 * @param card the card that has been revealed to all players
 * @author Generated Javadoc
 * @version 1.0
 */
public record CardRevealedEvent(Card card) implements GameEvent {

    /**
     * Retrieves the message type identifier for this event.
     * <p>
     * This identifier is used by the network messaging system to properly
     * route and handle the event when transmitted between client and server.
     * The message type allows the client to process the card revelation
     * appropriately in the user interface.
     *
     * @return MessageType.CARD_REVEALED_EVENT indicating the specific event type
     */
    @Override
    public MessageType eventType() {
        return MessageType.CARD_REVEALED_EVENT;
    }

    /**
     * Retrieves the visibility scope for this event.
     * <p>
     * Card revelation events are public information that all players must receive
     * to maintain synchronized game state. When a card is revealed, every player
     * needs to know what card is active so they can understand the current game
     * situation and make appropriate strategic decisions.
     * <p>
     * This public visibility ensures that no player has an unfair information
     * advantage regarding the active card and its effects.
     *
     * @return EventVisibility.ALL_PLAYERS indicating this event is visible to all participants
     */
    @Override
    public EventVisibility getVisibility() {
        return EventVisibility.ALL_PLAYERS;
    }

    /**
     * Retrieves the serialized arguments for network transmission.
     * <p>
     * Converts the card data into a format suitable for network communication
     * using the CardFactory serialization system. This ensures that all card
     * information (type, properties, effects, etc.) is properly transmitted
     * to all clients so they can display and process the revealed card correctly.
     * <p>
     * The serialization maintains the complete card state including any
     * dynamic properties or current conditions that might affect how the
     * card operates during the game.
     *
     * @return array containing the serialized card data for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{CardFactory.serializeCard(card)};
    }

}
