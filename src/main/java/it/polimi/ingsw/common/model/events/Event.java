package it.polimi.ingsw.common.model.events;

import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.model.events.CardPileLookedEvent;
import it.polimi.ingsw.model.events.CardRevealedEvent;
import it.polimi.ingsw.model.events.CardUpdatedEvent;
import it.polimi.ingsw.common.model.events.lobby.SetLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.List;

/**
 * Core interface representing an event in the system.
 */
public sealed interface Event permits
        UniqueEvent, BatchStartedEvent, BatchEndedEvent, SyncAllEvent,
        MatchStartedEvent, ComponentInsertedEvent, ComponentMovedEvent, ComponentPickedEvent, ShipBrokenEvent,
        ComponentReleasedEvent, ComponentReservedEvent, ComponentRotatedEvent, ComponentDestroyedEvent,
        HourglassMovedEvent, CardPileLookedEvent, CardPileReleasedEvent, CardRevealedEvent, CardUpdatedEvent,
        it.polimi.ingsw.client.model.events.CardPileLookedEvent, it.polimi.ingsw.client.model.events.CardRevealedEvent, it.polimi.ingsw.client.model.events.CardUpdatedEvent,
        FlightEndedEvent, SetLobbyEvent, JoinedLobbyEvent, LeftLobbyEvent, UsernameOkEvent
{

    /**
     * @return the message type associated with this event
     */
    MessageType eventType();
    /**
     * @return the visibility level of the event determining which players can receive it
     */
    EventVisibility getVisibility();
    /**
     * @return array of arguments associated with this event
     */
    Object[] getArgs();

    default List<String> getTargetPlayers() { return List.of(); } // Only for SPECIFIC_PLAYERS visibility

    default String getUniqueKey() { return null; }

    default void emitTo(EventCollector collector) {
        collector.addRegularEvent(this);
    }

}