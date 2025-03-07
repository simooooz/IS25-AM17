package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;

public class SabotageCard extends Card{
    public SabotageCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board){}
}
