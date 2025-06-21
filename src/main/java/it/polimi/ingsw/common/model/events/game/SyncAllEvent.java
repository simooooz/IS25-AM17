package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.dto.ModelDTO;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;

public record SyncAllEvent(ModelDTO dto) implements GameEvent {

    @Override
    public MessageType eventType() {
        return MessageType.SYNC_ALL_EVENT;
    }

    @Override
    public EventVisibility getVisibility() {
        return EventVisibility.PLAYER_ONLY;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{GameStateDTOFactory.serializeDTO(dto)};
    }

}
