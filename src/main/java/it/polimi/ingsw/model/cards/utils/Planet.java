package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.game.objects.ColorType;

import java.util.Map;

public class Planet {
    private final Map<ColorType, Integer> rewards;

    public Planet(Map<ColorType, Integer> rewards) {
        this.rewards = rewards;
    }

    public Map<ColorType, Integer> getRewards() {
        return rewards;
    }
}