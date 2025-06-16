package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.network.messages.MessageType;

public record BatchEndedEvent() implements GameEvent {

    @Override
    public MessageType eventType() { return MessageType.BATCH_END; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }

}
