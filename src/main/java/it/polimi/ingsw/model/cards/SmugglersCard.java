package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.objects.ColorType;

import java.util.List;

public class SmugglersCard extends Card{
    private final int firePower;
    private final int days;
    private final List<ColorType> goods;

    public SmugglersCard(int level, boolean isLearner, int firePower, int days, List<ColorType> goods) {
        super(level, isLearner);
        this.firePower = firePower;
        this.days = days;
        this.goods = goods;
    }

    public int getFirePower() {
        return firePower;
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

