package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event signaling the end of a batch of related events.
 * Marks the conclusion of a group of events that should be processed together.
 */
public record BatchEndedEvent() implements Event {

    @Override
    public MessageType eventType() { return MessageType.BATCH_END; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }

}
