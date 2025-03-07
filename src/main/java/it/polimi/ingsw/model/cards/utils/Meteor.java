package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.properties.DirectionType;

public class Meteor {
    private final boolean isBig;
    private final DirectionType directionForm;

    public Meteor(boolean isBig, DirectionType directionForm) {
        this.isBig = isBig;
        this.directionForm = directionForm;
    }

    public boolean getIsBig() {
        return isBig;
    }

    public DirectionType getDirectionForm() {
        return directionForm;
    }
}
