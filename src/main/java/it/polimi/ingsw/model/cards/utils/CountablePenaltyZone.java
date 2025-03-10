package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

public class CountablePenaltyZone extends PenaltyCombatZone {
    private final int penaltyNumber;
    private final MalusType penaltyType;

    public CountablePenaltyZone(int penaltyNumber, MalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    @Override
    public void resolve(Board board, PlayerData player) throws Exception {
        penaltyType.resolve(penaltyNumber, board, player);
    }

}