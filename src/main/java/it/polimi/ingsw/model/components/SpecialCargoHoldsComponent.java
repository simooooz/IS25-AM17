package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.CargoHoldsComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.GoodsUpdatedEvent;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Component implementation representing specialized cargo storage compartments on a player's ship.
 * Special cargo holds provide enhanced storage capabilities that can accommodate all types of goods,
 * including the dangerous or valuable red goods that standard cargo holds cannot store.
 * <p>
 * Special cargo holds are the premium storage solution in the game:
 * - Can store all good types including restricted red goods
 * - Have defined capacity limits that must be respected
 * - Provide dynamic loading and unloading capabilities
 * - Integrate with the ship's overall goods inventory tracking
 * - Emit events for real-time inventory updates
 */
public sealed class SpecialCargoHoldsComponent extends Component permits CargoHoldsComponent {

    /**
     * The maximum number of goods this cargo hold can store
     */
    private final int number;

    /**
     * The list of goods currently stored in this cargo hold
     */
    private final List<ColorType> goods;

    /**
     * Constructs a new SpecialCargoHoldsComponent with the specified properties.
     * Initializes the cargo hold with the specified capacity and an empty goods storage.
     *
     * @param id         the unique identifier for this cargo holds component
     * @param connectors the array of connector types defining how this component connects to others
     * @param number     the maximum storage capacity (number of goods that can be stored)
     */
    public SpecialCargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors);
        this.number = number;
        this.goods = new ArrayList<>();
    }

    /**
     * Retrieves the maximum storage capacity of this cargo hold.
     *
     * @return the maximum number of goods this cargo hold can store
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retrieves the list of goods currently stored in this cargo hold.
     * <p>
     * @return a list of ColorType values representing the stored goods
     */
    public List<ColorType> getGoods() {
        return goods;
    }

    /**
     * Loads a good into this cargo hold with capacity validation.
     * <p>
     * The loading process:
     * 1. Validates that the cargo hold has available capacity
     * 2. Adds the good to the cargo hold's storage
     * 3. Updates the ship's total goods inventory
     * 4. Emits events for real-time inventory tracking
     *
     * @param good the color type of the good to load into the cargo hold
     * @param ship the ship containing this cargo hold and overall inventory
     * @throws GoodNotValidException if the cargo hold is at full capacity
     */
    public void loadGood(ColorType good, Ship ship) {
        if (number == goods.size()) throw new GoodNotValidException("Cargo hold is full");
        goods.add(good);
        ship.getGoods().put(good, ship.getGoods().get(good) + 1);
        EventContext.emit(new GoodsUpdatedEvent(getId(), goods));
    }

    /**
     * Unloads a good from this cargo hold with availability validation.
     * <p>
     * The unloading process:
     * 1. Validates that the cargo hold contains the specified good
     * 2. Removes the good from the cargo hold's storage
     * 3. Updates the ship's total goods inventory
     * 4. Emits events for real-time inventory tracking.
     *
     * @param good the color type of the good to unload from the cargo hold
     * @param ship the ship containing this cargo hold and overall inventory
     * @throws GoodNotValidException if the cargo hold is empty or doesn't contain the specified good
     */
    public void unloadGood(ColorType good, Ship ship) {
        if (goods.isEmpty() || !goods.contains(good)) throw new GoodNotValidException("Cargo hold is empty");
        goods.remove(good);
        ship.getGoods().put(good, ship.getGoods().get(good) - 1);
        EventContext.emit(new GoodsUpdatedEvent(getId(), goods));
    }

    /**
     * Handles the effects of this cargo hold being destroyed, including goods loss.
     * <p>
     * When a cargo hold is destroyed:
     * 1. The standard component destruction process is completed
     * 2. All goods stored in the cargo hold are permanently lost
     * 3. The ship's total goods inventory is updated to reflect the losses
     *
     * @param player the player owning the ship where this component is being destroyed
     */
    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        for (ColorType good : goods) {
            player.getShip().getGoods().put(good, player.getShip().getGoods().get(good) - 1);
        }
    }

    /**
     * Checks if this component matches the specified type.
     * <p>
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches SpecialCargoHoldsComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == SpecialCargoHoldsComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     * <p>
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not compatible with SpecialCargoHoldsComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == SpecialCargoHoldsComponent.class || type.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast " + this.getClass().getName() + " to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this cargo holds component.
     * <p>
     *
     * @return a CargoHoldsComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new CargoHoldsComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), number, goods);
    }

}
