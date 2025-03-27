package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public abstract class PenaltyCombatZone {

    public PenaltyCombatZone() {}

    public abstract PlayerState resolve(Board board, PlayerData player);

    public void doCommandEffects(PlayerState commandType, Integer value) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        throw new RuntimeException("Method not valid");
    }

    public int getPenaltyNumber() {
        throw new RuntimeException("Method not valid");
    }

}
