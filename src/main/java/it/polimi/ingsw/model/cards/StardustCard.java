package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;

public class StardustCard extends Card{
    public StardustCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board){}
}
