package it.polimi.ingsw.common.model.events.game;

import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.events.EventVisibility;
import it.polimi.ingsw.common.model.events.UniqueEvent;
import it.polimi.ingsw.network.messages.MessageType;

import java.util.ArrayList;
import java.util.List;

public record GoodsUpdatedEvent(Integer id, List<ColorType> goods) implements UniqueEvent {

    @Override
    public MessageType eventType() { return MessageType.GOODS_UPDATED_EVENT; }

    @Override
    public EventVisibility getVisibility() { return EventVisibility.ALL_PLAYERS; }

    @Override
    public Object[] getArgs() {
        return new Object[]{ id, new ArrayList<>(goods) };
    }

    @Override
    public String getUniqueKey() {
        return eventType().toString().concat("_").concat(String.valueOf(id));
    }

}