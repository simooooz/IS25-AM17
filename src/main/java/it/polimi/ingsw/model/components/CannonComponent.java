package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.CannonComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

/**
 * Component implementation representing a cannon weapon system on a player's ship.
 * Cannon components provide offensive capabilities for combat encounters and defensive
 * actions against threats like meteors and enemy attacks.
 * <p>
 */
public final class CannonComponent extends Component {

    /**
     * The direction this cannon is oriented to fire toward
     */
    private DirectionType direction;

    /**
     * Whether this is a double cannon that provides enhanced firepower but requires battery power
     */
    private final boolean isDouble;

    /**
     * Constructs a new CannonComponent with the specified properties.
     *
     * @param id         the unique identifier for this cannon component
     * @param connectors the array of connector types defining how this component connects to others
     * @param direction  the direction this cannon is oriented to fire toward
     * @param isDouble   whether this is a double cannon requiring battery power for enhanced firepower
     */
    public CannonComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    /**
     * Retrieves the direction this cannon is oriented to fire toward.
     * <p>
     * @return the DirectionType indicating where this cannon is aimed
     */
    public DirectionType getDirection() {
        return direction;
    }

    /**
     * Retrieves whether this is a double cannon with enhanced capabilities.
     * <p>
     * @return true if this is a double cannon, false if it's a single cannon
     */
    public boolean getIsDouble() {
        return isDouble;
    }

    /**
     * Calculates the effective firepower of this cannon.
     * <p>
     * The power calculation considers two factors:
     * 1. Cannon type: Double cannons provide 2.0 base power, single cannons provide 1.0
     * 2. Direction effectiveness: North-facing cannons maintain full power,
     * while cannons facing other directions have their power halved due to less optimal positioning
     *
     * @return the calculated firepower of this cannon (0.5 to 2.0 range)
     */
    public double calcPower() {
        int factor = direction == DirectionType.NORTH ? 1 : 2;
        return (isDouble ? 2.0 : 1.0) / factor;
    }

    /**
     * Handles cannon rotation by updating both the component orientation and firing direction.
     * <p>
     * When a cannon is rotated, its firing direction must be updated to match the new
     * orientation.
     *
     * @param player    the player performing the rotation
     * @param rotations the number of 90-degree clockwise rotations to apply
     */
    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values(); // NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3
        this.direction = directions[((this.direction.ordinal() + rotations) % 4)];
    }

    /**
     * Validates that this cannon can be placed at its current position with proper firing clearance.
     * <p>
     * Cannon placement requires not only the standard component validation but also
     * verification that the firing line is clear. The cannon must have an empty space
     * in the direction it's oriented to fire.
     *
     * @param ship the ship where this cannon is being validated for placement
     * @return true if the cannon can be placed with clear firing line, false otherwise
     */
    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) &&
                (direction == DirectionType.NORTH && ship.getDashboard(y - 1, x).isEmpty()) ||
                (direction == DirectionType.EAST && ship.getDashboard(y, x + 1).isEmpty()) ||
                (direction == DirectionType.SOUTH && ship.getDashboard(y + 1, x).isEmpty()) ||
                (direction == DirectionType.WEST && ship.getDashboard(y, x - 1).isEmpty());
    }

    /**
     * Checks if this component matches the specified type.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches CannonComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == CannonComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not CannonComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == CannonComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast CannonComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this cannon component.
     *
     * @return a CannonComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new CannonComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), direction, isDouble);
    }

}
