package it.polimi.ingsw.model.cards.utils;

public class CountablePenaltyZone extends PenaltyCombatZone {
    private final int penaltyNumber;
    private final MalusType penaltyType;

    public CountablePenaltyZone(int penaltyNumber, MalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    public int getPenaltyNumber() {
        return penaltyNumber;
    }

    public MalusType getPenaltyType() {
        return penaltyType;
    }
}
