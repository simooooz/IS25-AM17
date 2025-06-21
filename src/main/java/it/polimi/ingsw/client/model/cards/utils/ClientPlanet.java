package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.Map;

public class ClientPlanet {

    @JsonProperty private Map<ColorType, Integer> rewards;

    public ClientPlanet() {}

    public Map<ColorType, Integer> getRewards() {
        return rewards;
    }

}
