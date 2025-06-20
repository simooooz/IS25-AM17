package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.Map;

public record Planet(@JsonProperty Map<ColorType, Integer> rewards) {

    public Planet(Map<ColorType, Integer> rewards) {
        this.rewards = rewards;
    }

    @Override
    public Map<ColorType, Integer> rewards() {
        return rewards;
    }

}