package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class Planet {

    private final @JsonProperty Map<ColorType, Integer> rewards;

    public Planet(Map<ColorType, Integer> rewards) {
        this.rewards = rewards;
    }

    public Map<ColorType, Integer> rewards() {
        return rewards;
    }

}