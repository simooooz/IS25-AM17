package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public abstract class PenaltyCombatZone {

    public PenaltyCombatZone() {}

    public abstract void resolve(Board board, PlayerData player) throws Exception;

}
