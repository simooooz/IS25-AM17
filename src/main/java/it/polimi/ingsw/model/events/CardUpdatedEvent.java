package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event record representing a change in the state of an active card.
 * This event is triggered when a card's internal state changes during gameplay,
 * requiring all players to receive the updated card information to maintain
 * synchronized game state.
 * <p>
 * Card updates occur in various gameplay situations:
 * - Changes in card progression through multi-stage encounters (like combat zones)
 * - Updates to dynamic card properties during execution (player counts, state tracking)
 * - Modifications to card effects based on player actions or choices
 * - State changes in complex cards that track ongoing progress or conditions
 * - Updates to card-specific variables that affect how the card operates
 * <p>
 * Unlike card revelation (which introduces a new card) or card completion (which
 * ends a card), card updates represent intermediate state changes that occur
 * while a card remains active. All players must receive these updates to ensure
 * they have accurate information about the current card's state and behavior.
 * <p>
 * This event is particularly important for complex cards like combat zones,
 * meteor swarms, or multi-phase encounters where the card's state evolves
 * as players take actions and the encounter progresses.
 *
 * @param card the card with updated state that all players need to receive
 * @author Generated Javadoc
 * @version 1.0
 */
public record CardUpdatedEvent(Card card) implements GameEvent {

    /**
     * Retrieves the message type identifier for this event.
     * <p>
     * This identifier is used by the network messaging system to properly
     * route and handle the event when transmitted between client and server.
     * The message type allows clients to distinguish card updates from other
     * card-related events and process them appropriately.
     *
     * @return MessageType.CARD_UPDATED_EVENT indicating the specific event type
     */
    @Override
    public MessageType eventType() {
        return MessageType.CARD_UPDATED_EVENT;
    }

    /**
     * Retrieves the visibility scope for this event.
     * <p>
     * Card update events are public information that all players must receive
     * to maintain synchronized understanding of the active card's current state.
     * When a card's state changes, every player needs to know the updated
     * information to make informed decisions and understand how the card
     * will behave in subsequent actions.
     * <p>
     * This public visibility ensures that all players have the same view of
     * the card's current state, preventing information asymmetries that could
     * affect fair gameplay.
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
     * Converts the updated card data into a format suitable for network
     * communication using the CardFactory serialization system. This ensures
     * that the complete current state of the card (including all recent changes)
     * is properly transmitted to all clients.
     * <p>
     * The serialization captures the card's updated state including any
     * modified properties, progress indicators, or internal variables that
     * have changed since the last transmission. This allows all clients to
     * synchronize their view of the card's current condition.
     *
     * @return array containing the serialized updated card data for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{CardFactory.serializeCard(card)};
    }

}
