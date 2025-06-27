package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.network.messages.MessageType;

/**
 * Event signaling the start of a batch of related events.
 * Used to group events that should be processed together.
 */
public record BatchStartedEvent() implements Event {

    @Override
    public MessageType eventType() { return MessageType.BATCH_START; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }

}
