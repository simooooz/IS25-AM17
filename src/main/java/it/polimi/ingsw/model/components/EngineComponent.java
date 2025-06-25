package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.dto.EngineComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

/**
 * Component implementation representing a propulsion engine on a player's ship.
 * Engine components provide movement capabilities for navigation through space
 * and advancement along the flight path during various encounters.
 * <p>
 * Engines have several important characteristics that affect their functionality:
 * - Power type: Single engines provide automatic propulsion, double engines provide enhanced power but require battery activation
 * - Direction: Engines have orientation but unlike cannons, their effectiveness is not direction-dependent
 * - Exhaust clearance: Engines require clear space behind them (south direction) for proper operation
 * <p>
 * The engine system supports strategic power management decisions:
 * - Single engines contribute automatically to ship movement capabilities
 * - Double engines offer more power but consume limited battery resources
 * - Engine aliens provide additional propulsion bonuses when any engines are active
 * <p>
 * Engine placement requires careful consideration of exhaust clearance, ensuring
 * that the propulsion systems can operate without obstruction from other ship components.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public final class EngineComponent extends Component {

    /**
     * The direction this engine is oriented (affects rotation but not power calculation)
     */
    private DirectionType direction;

    /**
     * Whether this is a double engine that provides enhanced power but requires battery activation
     */
    private final boolean isDouble;

    /**
     * Constructs a new EngineComponent with the specified properties.
     *
     * @param id         the unique identifier for this engine component
     * @param connectors the array of connector types defining how this component connects to others
     * @param direction  the direction this engine is oriented (primarily for rotation tracking)
     * @param isDouble   whether this is a double engine requiring battery power for enhanced propulsion
     */
    public EngineComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    /**
     * Retrieves whether this is a double engine with enhanced capabilities.
     * <p>
     * Double engines differ from single engines in several ways:
     * - They provide twice the propulsion power of single engines
     * - They require battery power to activate (unlike single engines which operate automatically)
     * - They allow strategic choice in movement situations
     * - They are more effective for players with sufficient power resources
     *
     * @return true if this is a double engine, false if it's a single engine
     */
    public boolean getIsDouble() {
        return isDouble;
    }

    /**
     * Calculates the propulsion power of this engine.
     * <p>
     * Unlike cannons, engine power calculation is straightforward:
     * - Double engines provide 2 units of propulsion power
     * - Single engines provide 1 unit of propulsion power
     * - Direction does not affect engine power (all orientations are equally effective)
     * <p>
     * This power value is used in movement calculations, open space navigation,
     * and other situations where propulsion capability matters.
     *
     * @return the propulsion power of this engine (1 for single, 2 for double)
     */
    public int calcPower() {
        return isDouble ? 2 : 1;
    }

    /**
     * Handles engine rotation by updating both the component orientation and direction tracking.
     * <p>
     * When an engine is rotated, its direction property is updated to match the new
     * orientation. While direction doesn't affect engine power, it's important for
     * proper exhaust clearance validation and visual representation.
     *
     * @param player    the player performing the rotation
     * @param rotations the number of 90-degree clockwise rotations to apply
     */
    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + rotations) % 4)];
    }

    /**
     * Validates that this engine can be placed at its current position with proper exhaust clearance.
     * <p>
     * Engine placement requires not only the standard component validation but also
     * verification that the exhaust path is clear. Engines must have an empty space
     * to the south (behind them) to ensure proper exhaust flow and prevent obstruction
     * from other ship components.
     * <p>
     * This validation ensures realistic engine operation constraints and prevents
     * players from placing engines in positions where they would be blocked by
     * their own ship structure.
     *
     * @param ship the ship where this engine is being validated for placement
     * @return true if the engine can be placed with clear exhaust path, false otherwise
     */
    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) && (direction == DirectionType.SOUTH && ship.getDashboard(y + 1, x).isEmpty());
    }

    /**
     * Checks if this component matches the specified type.
     * <p>
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * Only returns true for EngineComponent class checks.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches EngineComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == EngineComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     * <p>
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. Only supports
     * casting to EngineComponent class.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not EngineComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == EngineComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast EngineComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this engine component.
     * <p>
     * The DTO includes all relevant information about the engine's current
     * state, including its orientation, power type, position, and connection
     * information. This is used for client-server communication and UI
     * display purposes.
     *
     * @return an EngineComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new EngineComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), direction, isDouble);
    }

}
