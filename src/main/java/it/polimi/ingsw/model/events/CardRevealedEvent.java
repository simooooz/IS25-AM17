package it.polimi.ingsw.model.events;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event record representing the public revelation of a card to all players.
 * This event is triggered when a card is drawn, activated, or otherwise revealed
 * during normal game flow where all players need to see the same card information.
 * <p>
 */
public record CardRevealedEvent(Card card) implements Event {

    /**
     * Retrieves the message type identifier for this event.
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
     * to maintain synchronized game state.
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
     * @return array containing the serialized card data for network transmission
     */
    @Override
    public Object[] getArgs() {
        return new Object[]{CardFactory.serializeCard(card)};
    }

}
