package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public class CountablePenaltyZone extends PenaltyCombatZone {
    private final int penaltyNumber;
    private final MalusType penaltyType;

    public CountablePenaltyZone(int penaltyNumber, MalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    @Override
    public PlayerState resolve(ModelFacade model, Board board, PlayerData player) {
        return penaltyType.resolve(penaltyNumber, board, player);
    }

    @Override
    public int getPenaltyNumber() {
        return penaltyNumber;
    }

    @Override
    public String toString() {
        return penaltyNumber + " " + penaltyType.toString();
    }

}