package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;


import java.util.List;
import java.util.Optional;

/**
 * Utility class representing a meteor in a meteor swarm encounter.
 * This class handles the mechanics of meteor impacts against player ships,
 * including trajectory calculation, defensive options, and component destruction.
 * <p>
 * Meteors come in two sizes with different characteristics and defensive options:
 * - Small meteors: Can be deflected by shields or avoided if the impact site lacks connectors
 * - Big meteors: Cannot be deflected by shields, affect wider areas, but can be destroyed by cannons
 * <p>
 * The meteor system uses coordinate-based targeting where dice rolls determine
 * the specific impact location, creating dynamic space hazard scenarios that
 * require different defensive strategies based on meteor size and ship configuration.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class Meteor {

    /**
     * Whether this meteor is a big meteor with enhanced destructive capability
     */
    @JsonProperty
    private final boolean isBig;

    /**
     * The direction from which this meteor approaches the target ship
     */
    @JsonProperty
    private final DirectionType directionFrom;

    /**
     * Constructs a new Meteor with the specified characteristics.
     *
     * @param isBig         whether this is a big meteor with enhanced impact effects
     * @param directionFrom the direction from which the meteor approaches the target
     */
    public Meteor(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    /**
     * Executes the meteor impact against a player's ship at the specified coordinates.
     * <p>
     * The impact resolution follows different logic based on meteor size:
     * <p>
     * For small meteors:
     * 1. Validates coordinates are within valid range (4-10)
     * 2. Identifies target components in the meteor's trajectory
     * 3. Checks if impact site has empty connectors (natural protection)
     * 4. Offers shield defense if ship has directional protection and batteries
     * 5. Destroys component if no defenses are available
     * <p>
     * For big meteors:
     * 1. Validates coordinates and identifies primary target
     * 2. Expands target area to include adjacent coordinates (except from North)
     * 3. Searches for defensive cannons oriented toward the meteor's direction
     * 4. Prioritizes single cannons (automatic destruction) over double cannons
     * 5. Offers cannon defense choice if double cannons are available
     * 6. Destroys component if no cannon defenses are present
     *
     * @param player the player data for the target of the meteor impact
     * @param coord  the coordinate position where the meteor impacts (4-10 range)
     * @return PlayerState.DONE if impact is resolved immediately,
     * PlayerState.WAIT_SHIELD for small meteor shield defense,
     * PlayerState.WAIT_CANNONS for big meteor cannon defense,
     * or the result of component destruction
     */
    public PlayerState hit(PlayerData player, int coord) {
        if (coord > 10 || coord < 4) return PlayerState.DONE; // Miss
        Ship ship = player.getShip();

        List<Component> targets = getTargets(ship, coord); // Find hit component
        if (targets.isEmpty()) return PlayerState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && target.getConnectors()[directionFrom.ordinal()] == ConnectorType.EMPTY) // Ship is safe
            return PlayerState.DONE;
        else if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0)// Ask user if he wants to use a battery
            return PlayerState.WAIT_SHIELD;
        else if (isBig) {

            if (directionFrom != DirectionType.NORTH) {
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord - 1));
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord + 1));
            }

            List<CannonComponent> cannonsOverLine = targets.stream()
                    .filter(c -> c.matchesType(CannonComponent.class))
                    .map(c -> c.castTo(CannonComponent.class))
                    .filter(c -> c.getDirection() == directionFrom)
                    .toList();

            Optional<CannonComponent> singleCannon = cannonsOverLine.stream().filter(c -> !c.getIsDouble()).findFirst();
            if (singleCannon.isPresent()) return PlayerState.DONE;

            if (!cannonsOverLine.isEmpty()) // There is a double cannon that could destroy meteor
                return PlayerState.WAIT_CANNONS;

        }
        return target.destroyComponent(player); // Destroy component
    }

    /**
     * Retrieves the list of components that would be targeted by this meteor
     * at the specified coordinates.
     * <p>
     * This method performs target identification without executing the impact,
     * useful for validation, preview, or defensive planning. It follows the
     * same trajectory calculation as the hit method but only returns the
     * target components without applying damage.
     *
     * @param ship  the ship to check for target components
     * @param coord the coordinate position where the meteor would impact
     * @return List of components in the meteor's trajectory at the specified coordinates
     */
    public List<Component> getTargets(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord);
    }

    /**
     * Retrieves the direction from which this meteor approaches targets.
     * <p>
     * This accessor method is useful for defensive calculations, cannon
     * orientation validation, and shield protection verification.
     *
     * @return the DirectionType indicating the meteor's approach direction
     */
    public DirectionType getDirectionFrom() {
        return directionFrom;
    }

}