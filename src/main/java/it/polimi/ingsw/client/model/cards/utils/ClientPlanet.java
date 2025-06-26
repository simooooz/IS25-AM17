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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClientPlanet that = (ClientPlanet) obj;

        if (rewards == null && that.rewards == null) return true;
        if (rewards == null || that.rewards == null) return false;
        if (rewards.size() != that.rewards.size()) return false;

        return rewards.equals(that.rewards);
    }

}
