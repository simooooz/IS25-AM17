package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.Map;

/**
 * Utility class representing a planet that players can land on during planetary encounters.
 * This class encapsulates the trading opportunities and resource rewards available
 * on a specific planet, defining what goods players can acquire.
 */
@SuppressWarnings("ClassCanBeRecord")
public class Planet {

    /**
     * Map defining the goods rewards available on this planet by color type and quantity
     */
    private final @JsonProperty Map<ColorType, Integer> rewards;

    /**
     * Constructs a new Planet with the specified reward structure.
     * <p>
     *
     * @param rewards the map of goods rewards by color type and quantity available on this planet
     */
    public Planet(Map<ColorType, Integer> rewards) {
        this.rewards = rewards;
    }

    /**
     * Retrieves the goods rewards available on this planet.
     * <p>
     *
     * @return an immutable map of color types to quantities representing available goods rewards
     */
    public Map<ColorType, Integer> rewards() {
        return rewards;
    }

}