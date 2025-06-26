package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.Map;

/**
 * Utility class representing a planet that players can land on during planetary encounters.
 * This class encapsulates the trading opportunities and resource rewards available
 * on a specific planet, defining what goods players can acquire through planetary commerce.
 * <p>
 * Each planet offers a unique combination of goods that players can obtain by landing
 * and conducting trade. The reward structure determines both the types and quantities
 * of goods available, creating strategic choices for players about which planets
 * are most valuable for their current needs.
 * <p>
 * Planets serve as trading hubs in the game economy, providing players with
 * opportunities to acquire specific resources that may be difficult to obtain
 * through other means. The reward system allows for diverse planetary characteristics,
 * from resource-rich worlds to specialized trading outposts.
 *
 * @author Generated Javadoc
 * @version 1.0
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
     * The rewards map defines what goods are available for trade on this planet,
     * with each color type mapped to the quantity available. This structure
     * allows planets to offer diverse trading opportunities, from single-resource
     * worlds to complex multi-good trading hubs.
     *
     * @param rewards the map of goods rewards by color type and quantity available on this planet
     */
    public Planet(Map<ColorType, Integer> rewards) {
        this.rewards = rewards;
    }

    /**
     * Retrieves the goods rewards available on this planet.
     * <p>
     * This method provides access to the complete trading catalog for the planet,
     * allowing players and game systems to understand what goods can be acquired
     * through planetary commerce. The returned map defines both the types of
     * goods available and their respective quantities.
     * <p>
     * This information is used for:
     * - Validating player trading requests during planetary encounters
     * - Displaying available trading options to players
     * - Calculating strategic value of different planetary landing choices
     *
     * @return an immutable map of color types to quantities representing available goods rewards
     */
    public Map<ColorType, Integer> rewards() {
        return rewards;
    }

}