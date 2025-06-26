package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.network.messages.MessageType;

public record HourglassMovedEvent() implements Event {

    @Override
    public MessageType eventType() { return MessageType.HOURGLASS_MOVED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }

}
