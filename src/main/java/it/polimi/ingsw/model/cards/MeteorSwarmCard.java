package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.game.Board;

import java.util.List;

public class MeteorSwarmCard extends Card{

    private final List<Meteor> meteors;

    public MeteorSwarmCard(int level, boolean isLearner, List<Meteor> meteors) {
        super(level, isLearner);
        this.meteors = meteors;
    }

    public List<Meteor> getMeteors() {
        return meteors;
    }

    @Override
    public void resolve(Board board){}
}
