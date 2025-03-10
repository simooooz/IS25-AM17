package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.Dice;
import it.polimi.ingsw.model.player.PlayerData;

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
    public void resolve(Board board) throws Exception {
        for (Meteor meteor : meteors) {
            int coord = Dice.roll() + Dice.roll(); // View => leader rolls dices
            for (PlayerData playerData : board.getPlayersByPos()) {
                meteor.hit(playerData.getShip(), coord);
            }
        }
    }

}
