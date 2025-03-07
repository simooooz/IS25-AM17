package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.properties.DirectionType;

public class CannonFire {

    private final boolean isBig;
    private final DirectionType directionFrom;

    public CannonFire(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public boolean getIsBig() {
        return isBig;
    }

    public DirectionType getDirectionFrom() {
        return directionFrom;
    }
}


