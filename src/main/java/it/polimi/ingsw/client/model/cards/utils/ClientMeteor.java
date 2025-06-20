package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class ClientMeteor {

    @JsonProperty private boolean isBig;
    @JsonProperty private DirectionType directionFrom;

    public ClientMeteor() {}

    @Override
    public String toString() {
        String arrow = switch (directionFrom) {
            case NORTH -> "↓";
            case SOUTH -> "↑";
            case EAST -> "←";
            case WEST -> "→";
        };
        return  (isBig ? Constants.inTheMiddle("☄️☄️ ", 7)
                : Constants.inTheMiddle("☄️ ", 7)) + arrow;
    }

}