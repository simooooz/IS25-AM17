package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.common.model.events.game.*;

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
