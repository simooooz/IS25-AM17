package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.game.Board;

import java.util.List;

public class PiratesCard extends Card{
    private final int firePower;
    private final int credits;
    private final int days;
    private final List<CannonFire> cannonFires;

    public PiratesCard(int level, boolean isLearner, int firePower, int credits, int days, List<CannonFire> cannonFires) {
        super(level, isLearner);
        this.firePower = firePower;
        this.credits = credits;
        this.days = days;
        this.cannonFires = cannonFires;
    }

    public int getFirePower() {
        return firePower;
    }

    public int getCredits() {
        return credits;
    }

    public int getDays() {
        return days;
    }

    public List<CannonFire> getCannonFires() {
        return cannonFires;
    }

    @Override
    public void resolve(Board board){}
}
