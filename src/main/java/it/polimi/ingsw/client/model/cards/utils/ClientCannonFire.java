package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class ClientCannonFire {

    @JsonProperty private boolean isBig;
    @JsonProperty private DirectionType directionFrom;

    public ClientCannonFire(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public ClientCannonFire() {}

    @Override
    public String toString() {
        String fire = "\uD83D\uDD25";
        String arrow = switch (directionFrom) {
            case NORTH -> "↓";
            case SOUTH -> "↑";
            case EAST -> "←";
            case WEST -> "→";
        };
        return  (isBig ? Constants.inTheMiddle(fire + fire + " ", 7)
                : Constants.inTheMiddle(fire + " ", 7)) + arrow;
    }

}
