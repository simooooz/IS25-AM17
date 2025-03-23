package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.CardState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public abstract class PenaltyCombatZone {

    public PenaltyCombatZone() {}

    public abstract CardState resolve(Board board, PlayerData player) throws Exception;

    public void doCommandEffects(CardState commandType, Integer value) {}

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {}

}
