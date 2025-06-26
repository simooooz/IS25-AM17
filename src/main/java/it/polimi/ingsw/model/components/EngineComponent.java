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
     * @return true if this is a double engine, false if it's a single engine
     */
    public boolean getIsDouble() {
        return isDouble;
    }

    /**
     * Calculates the propulsion power of this engine.
     * <p>
     * @return the propulsion power of this engine (1 for single, 2 for double)
     */
    public int calcPower() {
        return isDouble ? 2 : 1;
    }

    /**
     * Handles engine rotation by updating both the component orientation and direction tracking.
     * <p>
     * When an engine is rotated, its direction property is updated to match the new
     * orientation.
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
     * @return an EngineComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new EngineComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), direction, isDouble);
    }

}
