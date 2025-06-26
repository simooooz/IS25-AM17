package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event record representing a change in the state of an active card.
 * This event is triggered when a card's internal state changes during gameplay,
 * requiring all players to receive the updated card information to maintain
 * synchronized game state.
 *
 * @param card the card with updated state that all players need to receive
 * @author Generated Javadoc
 * @version 1.0
 */
public record CardUpdatedEvent(Card card) implements Event {

    /**
     * Retrieves the message type identifier for this event.
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
     * @return array containing the serialized updated card data for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{CardFactory.serializeCard(card)};
    }

}
