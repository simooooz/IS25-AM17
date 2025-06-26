package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.BatteryComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.game.BatteriesUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

/**
 * Component implementation representing a battery power source on a player's ship.
 * Battery components provide energy storage that can be consumed to power various
 * ship systems including double engines, double cannons, and defensive shields.
 * <p>
 * Batteries come in two variants with different power capacities:
 * - Standard batteries: Provide 2 energy units
 * - Triple batteries: Provide 3 energy units for enhanced power needs
 */
public final class BatteryComponent extends Component {

    /**
     * The current number of battery charges remaining in this component
     */
    private int batteries;

    /**
     * Whether this is a triple battery component with enhanced capacity
     */
    private final boolean isTriple;

    /**
     * Constructs a new BatteryComponent with the specified properties.
     * Initializes the battery charge based on the component type:
     * triple batteries start with 3 charges, standard batteries start with 2 charges.
     *
     * @param id         the unique identifier for this battery component
     * @param connectors the array of connector types defining how this component connects to others
     * @param isTriple   whether this is a triple battery component with enhanced capacity
     */
    public BatteryComponent(int id, ConnectorType[] connectors, boolean isTriple) {
        super(id, connectors);
        this.isTriple = isTriple;
        this.batteries = isTriple ? 3 : 2;
    }

    /**
     * Retrieves the current number of battery charges remaining in this component.
     * <p>
     * This value represents the available energy that can be consumed for
     * powering ship systems. Once depleted to zero, the battery component
     * can no longer provide power until recharged.
     *
     * @return the number of battery charges currently available
     */
    public int getBatteries() {
        return batteries;
    }

    /**
     * Consumes one battery charge from this component to power ship systems.
     * <p>
     * This method handles the permanent consumption of battery power, reducing
     * both the component's charge count and the ship's total available battery
     * power.
     * <p>
     * An event is emitted to notify the game system of the battery usage,
     * allowing UI updates and other systems to react to the power consumption.
     *
     * @param ship the ship containing this battery component, used to update total battery count
     * @throws BatteryComponentNotValidException if the battery component has no remaining charges
     */
    public void useBattery(Ship ship) {
        if (batteries == 0) throw new BatteryComponentNotValidException("Not enough batteries");
        batteries--;
        ship.setBatteries(ship.getBatteries() - 1);
        EventContext.emit(new BatteriesUpdatedEvent(getId(), batteries));
    }

    /**
     * Handles the insertion of this battery component into a player's ship.
     * <p>
     * When a battery component is added to the ship, its power capacity
     * is added to the ship's total available battery power. This allows
     * the ship to utilize the energy stored in this component for various
     * operations requiring power.
     *
     * @param player    the player owning the ship where this component is being inserted
     * @param row       the row position where the component is being placed
     * @param col       the column position where the component is being placed
     * @param rotations the number of rotations applied to the component orientation
     * @param weld      whether the component is being welded permanently to the ship
     */
    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        super.insertComponent(player, row, col, rotations, weld);
        player.getShip().setBatteries(player.getShip().getBatteries() + batteries);
    }

    /**
     * Handles the effects of this battery component being destroyed.
     * <p>
     * When a battery component is destroyed (through damage, asteroid impacts, etc.),
     * its remaining power capacity is removed from the ship's total available
     * battery power. This represents the loss of the energy storage capability
     * that the component provided to the ship's power systems.
     *
     * @param player the player owning the ship where this component is being destroyed
     */
    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        player.getShip().setBatteries(player.getShip().getBatteries() - batteries);
    }

    /**
     * Checks if this component matches the specified type.
     * <p>
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * Only returns true for BatteryComponent class checks.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches BatteryComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == BatteryComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     * <p>
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. Only supports
     * casting to BatteryComponent class.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not BatteryComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == BatteryComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast BatteryComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this battery component.
     * <p>
     * The DTO includes all relevant information about the battery component's
     * current state, including its power capacity type, remaining charges,
     * position, and connection status. This is used for client-server
     * communication and UI display purposes.
     *
     * @return a BatteryComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new BatteryComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), isTriple, batteries);
    }

}
