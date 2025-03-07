package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.objects.ColorType;

import java.util.List;

public class AbandonedStationCard extends Card{
    private final int crew;
    private final int days;
    private final List<ColorType> goods;

    public AbandonedStationCard(int level, boolean isLearner, int crew, int days, List<ColorType> goods) {
        super(level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    public int getCrew() {
        return crew;
    }

    public int getDays() {
        return days;
    }

    public List<ColorType> getGoods() {
        return goods;
    }

    @Override
    public void resolve(){}
}
