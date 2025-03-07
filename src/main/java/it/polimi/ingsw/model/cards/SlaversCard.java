package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;

public class SlaversCard extends Card{
    private final int crew;
    private final int credits;
    private final int days;
    private final int firePower;

    public SlaversCard(int level, boolean isLearner, int crew, int credits, int days, int firePower) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.firePower = firePower;
    }

    public int getCrew() {
        return crew;
    }

    public int getCredits() {
        return credits;
    }

    public int getDays() {
        return days;
    }

    public int getFirePower() {
        return firePower;
    }

    @Override
    public void resolve(Board board){}
}
