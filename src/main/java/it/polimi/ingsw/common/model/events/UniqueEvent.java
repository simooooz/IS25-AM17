package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.common.model.events.game.*;

/**
 * Interface extending Event to represent events that must be
 * uniquely identifiable within the system.
 */
public sealed interface UniqueEvent extends Event permits
        ErrorEvent, PlayersPositionUpdatedEvent, PlayersStateUpdatedEvent,
        BatteriesUpdatedEvent, CrewUpdatedEvent, GoodsUpdatedEvent, CreditsUpdatedEvent
{

    @Override
    default String getUniqueKey() {
        return eventType().toString();
    }

    @Override
    default void emitTo(EventCollector collector) {
        collector.addUniqueEvent(this);
    }
    
}
