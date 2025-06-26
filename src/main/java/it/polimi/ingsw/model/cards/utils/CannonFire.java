package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.util.List;
import java.util.Optional;

/**
 * Utility class representing a cannon fire attack in the game.
 * This class handles the mechanics of incoming projectile attacks against player ships,
 * including trajectory calculation, hit detection, shield interaction, and component destruction.
 * <p>
 * Cannon fire attacks have different characteristics based on their size and direction:
 * - Big cannon fires cannot be deflected by shields and always cause damage if they hit
 * - Small cannon fires can be deflected by powered shields if the ship has protection on the attack side
 * - All cannon fires follow directional trajectories and only hit components in their path
 * <p>
 * The attack system uses coordinate-based targeting where dice rolls determine the
 * specific impact location, creating dynamic and unpredictable combat scenarios.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CannonFire {

    /** Whether this cannon fire is a big projectile that cannot be deflected by shields */
    @JsonProperty private final boolean isBig;

    /** The direction from which this cannon fire approaches the target ship */
    @JsonProperty private final DirectionType directionFrom;

    /**
     * Constructs a new CannonFire with the specified characteristics.
     *
     * @param isBig whether this is a big cannon fire that bypasses shield protection
     * @param directionFrom the direction from which the cannon fire approaches the target
     */
    public CannonFire(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    /**
     * Executes the cannon fire attack against a player's ship at the specified coordinates.
     * <p>
     * The attack resolution follows these steps:
     * 1. Validates that the coordinates are within the valid target range (4-10)
     * 2. Identifies target components in the projectile's path from the attack direction
     * 3. Determines if the attack can be deflected by shields (small projectiles only)
     * 4. Either prompts for shield usage or immediately destroys the target component
     * <p>
     * Shield deflection is only possible for small cannon fires when:
     * - The ship has protection on the side being attacked
     * - The ship has available battery power to activate shields
     * - The projectile is not a big cannon fire (which bypasses all shields)
     *
     * @param player the player data for the target of the cannon fire
     * @param coord the coordinate position where the cannon fire impacts (4-10 range)
     * @return PlayerState.DONE if the attack misses or is resolved immediately,
     *         PlayerState.WAIT_SHIELD if shield deflection is possible,
     *         or the result of component destruction if the attack hits
     */
    public PlayerState hit(PlayerData player, int coord) {
        if (coord > 10 || coord < 4) return PlayerState.DONE; // Miss
        Ship ship = player.getShip();

        List<Component> targets = directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targets.isEmpty()) return PlayerState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) // Ask user if he wants to use a battery
            return PlayerState.WAIT_SHIELD;

        return target.destroyComponent(player); // Destroy component
    }

    /**
     * Retrieves the target component that would be hit by this cannon fire at the specified coordinates.
     * <p>
     * This method performs target identification without executing the attack, useful for
     * validation, preview, or conditional logic. It follows the same trajectory calculation
     * as the hit method but only returns the target component without applying damage.
     *
     * @param ship the ship to check for target components
     * @param coord the coordinate position where the cannon fire would impact
     * @return Optional containing the target component if one exists at the trajectory,
     *         or Optional.empty() if no component would be hit
     */
    public Optional<Component> getTarget(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord).stream().findFirst();
    }

}