package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;

abstract public class Card {

    private final int level;            // card level (1 or 2)
    private final boolean isLearner;    // learning flight

    public Card(int level, boolean isLearner) {
        this.level = level;
        this.isLearner = isLearner;
    }

    public int getLevel() {
        return level;
    }

    public boolean getIsLearner() {
        return isLearner;
    }

    public void resolve(Board board) throws Exception {};

}
