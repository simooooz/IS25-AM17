package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

abstract public class Card {

    private final int level;
    private final boolean isLearner;
    Map<String, CardState> playersState;

    public Card(int level, boolean isLearner) {
        this.level = level;
        this.isLearner = isLearner;
        this.playersState = new HashMap<>();
    }

    public int getLevel() {
        return level;
    }

    public boolean getIsLearner() {
        return isLearner;
    }

    public void resolve(Board board) throws Exception {}

}
