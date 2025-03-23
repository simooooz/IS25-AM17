package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.CardState;
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
    public CardState resolve(Board board, PlayerData player) throws Exception {
        return penaltyType.resolve(penaltyNumber, board, player);
    }

}