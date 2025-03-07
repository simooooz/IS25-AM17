package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.game.objects.ColorType;

import java.util.List;

public class Planet {
    private final List<ColorType> rewards;

    public Planet(List<ColorType> rewards) {
        this.rewards = rewards;
    }

    public List<ColorType> getRewards() {
        return rewards;
    }
}