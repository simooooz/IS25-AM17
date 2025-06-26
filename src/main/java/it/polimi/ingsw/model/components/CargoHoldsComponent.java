package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.CargoHoldsComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.Ship;

/**
 * Component implementation representing standard cargo storage compartments on a player's ship.
 * Cargo holds provide basic goods storage capabilities but with important limitations
 * compared to their specialized counterparts.
 * <p>
MO * This component extends SpecialCargoHoldsComponent to inherit the basic storage
 * mechanics while adding the red goods restriction through method overrides.
 */
public final class CargoHoldsComponent extends SpecialCargoHoldsComponent {

    /**
     * Constructs a new CargoHoldsComponent with the specified properties.
     * Inherits the basic cargo storage functionality from SpecialCargoHoldsComponent
     * while enforcing restrictions on red goods storage.
     *
     * @param id         the unique identifier for this cargo holds component
     * @param connectors the array of connector types defining how this component connects to others
     * @param number     the storage capacity (number of goods that can be stored)
     */
    public CargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors, number);
    }

    /**
     * Loads a good into this cargo hold with red goods restriction enforcement.
     * <p>
     * This method extends the parent loading functionality by adding validation
     * to prevent red goods from being stored in standard cargo holds.
     *
     * @param good the color type of the good to load into the cargo hold
     * @param ship the ship containing this cargo hold component
     * @throws GoodNotValidException if attempting to load red goods into a standard cargo hold
     */
    @Override
    public void loadGood(ColorType good, Ship ship) {
        if (good == ColorType.RED) throw new GoodNotValidException("Red good in normal hold");
        super.loadGood(good, ship);
    }

    /**
     * Unloads a good from this cargo hold with red goods restriction enforcement.
     * <p>
     * This method extends the parent unloading functionality by adding validation
     * to prevent red goods operations on standard cargo holds.
     *
     * @param good the color type of the good to unload from the cargo hold
     * @param ship the ship containing this cargo hold component
     * @throws GoodNotValidException if attempting to unload red goods from a standard cargo hold
     */
    @Override
    public void unloadGood(ColorType good, Ship ship) {
        if (good == ColorType.RED) throw new GoodNotValidException("Red good in normal hold");
        super.unloadGood(good, ship);
    }

    /**
     * Checks if this component matches the specified type.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches CargoHoldsComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == CargoHoldsComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not compatible with CargoHoldsComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == CargoHoldsComponent.class ||
                type == SpecialCargoHoldsComponent.class ||
                type.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast CargoHoldsComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this cargo holds component.
     * <p>
     *
     * @return a CargoHoldsComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new CargoHoldsComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), getNumber(), getGoods());
    }

}
