package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;

public class OpenSpaceCard extends Card{
    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board){}
}
