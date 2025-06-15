package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public abstract class PenaltyCombatZone {

    public PenaltyCombatZone() {}

    public abstract PlayerState resolve(ModelFacade model, Board board, PlayerData player);

    public void doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public int getPenaltyNumber() {
        throw new RuntimeException("Method not valid");
    }

}
