package it.polimi.ingsw.model.cards;

public class AbandonedShipCard extends Card{
    private final int crew;
    private final int credits;
    private final int days;

    public AbandonedShipCard(int level, boolean isLearner, int crew, int credits, int days) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
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

    @Override
    public void resolve(){}
}
