package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.CabinComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.game.CrewUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

/**
 * Component implementation representing crew quarters on a player's ship.
 * Cabin components house the ship's crew members, including both human crew
 * and alien specialists that provide enhanced ship capabilities.
 * <p>
 * Cabins serve multiple important functions in the game:
 * - Housing human crew members who contribute to overall ship operations
 * - Accommodating alien specialists who provide specific bonuses (cannon or engine)
 * - Contributing to the ship's total crew count for survival and capability checks
 * - Providing targets for epidemic spread and crew-based encounter requirements
 * <p>
 * There are two types of cabin components:
 * - Starting cabins: Initial crew quarters that cannot house aliens
 * - Regular cabins: Standard crew quarters that can accommodate either humans or aliens
 * <p>
 * The cabin system enforces important constraints: aliens can only be placed
 * where compatible odd components exist nearby, and only one alien of each
 * type can exist per ship, representing specialized crew roles.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public final class CabinComponent extends Component {

    /**
     * The number of human crew members currently housed in this cabin
     */
    private int humans;

    /**
     * The type of alien specialist housed in this cabin, or null if none
     */
    private AlienType alien;

    /**
     * Whether this is a starting cabin component that comes with the initial ship
     */
    private final boolean isStarting;

    /**
     * Constructs a new CabinComponent with the specified properties.
     * Initializes the cabin with 2 human crew members and no alien occupant.
     *
     * @param id         the unique identifier for this cabin component
     * @param connectors the array of connector types defining how this component connects to others
     * @param isStarting whether this is a starting cabin that cannot house aliens
     */
    public CabinComponent(int id, ConnectorType[] connectors, boolean isStarting) {
        super(id, connectors);
        this.humans = 2;
        this.alien = null;
        this.isStarting = isStarting;
    }

    /**
     * Retrieves whether this is a starting cabin component.
     * <p>
     * Starting cabins are part of the ship's initial configuration and have
     * special restrictions: they cannot house alien crew members and are
     * automatically placed and welded when the ship is initialized.
     *
     * @return true if this is a starting cabin, false otherwise
     */
    public boolean getIsStarting() {
        return isStarting;
    }

    /**
     * Retrieves the current number of human crew members in this cabin.
     *
     * @return the number of human crew members currently housed in this cabin
     */
    public int getHumans() {
        return humans;
    }

    /**
     * Sets the number of human crew members in this cabin.
     * <p>
     * This method handles crew changes by:
     * - Validating that the cabin is properly placed on the ship
     * - Removing any alien occupant if humans are being added (mutual exclusivity)
     * - Updating the ship's total crew count accordingly
     * - Emitting events to notify other systems of crew changes
     * <p>
     * Human and alien crew are mutually exclusive in each cabin, representing
     * the specialized nature of alien crew roles that require dedicated quarters.
     *
     * @param humans the new number of human crew members (minimum 0)
     * @param ship   the ship containing this cabin component
     * @throws ComponentNotValidException if the cabin is not properly placed on the ship
     */
    public void setHumans(int humans, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Cabin component not valid");
        if (humans < 0) humans = 0;
        if (alien != null) setAlien(null, ship);
        int delta = humans - this.humans;
        ship.setCrew(ship.getCrew() + delta);
        this.humans = humans;

        EventContext.emit(new CrewUpdatedEvent(getId(), humans, alien));
    }

    /**
     * Retrieves the type of alien specialist housed in this cabin.
     *
     * @return an Optional containing the alien type if present, or empty if no alien
     */
    public Optional<AlienType> getAlien() {
        return Optional.ofNullable(alien);
    }

    /**
     * Sets or removes an alien specialist in this cabin.
     * <p>
     * This method handles complex alien placement logic including:
     * - Validating that the cabin is properly placed on the ship
     * - Preventing alien placement in starting cabins
     * - Checking for compatible odd components in adjacent areas
     * - Enforcing the one-alien-per-type rule across the entire ship
     * - Managing crew count changes and ship capability bonuses
     * - Removing human crew when aliens are placed (mutual exclusivity)
     * <p>
     * Aliens provide ship-wide bonuses: cannon aliens enhance firepower,
     * engine aliens enhance propulsion. Each ship can have at most one
     * alien of each type, representing specialized crew positions.
     *
     * @param newAlien the type of alien to place, or null to remove current alien
     * @param ship     the ship containing this cabin component
     * @throws ComponentNotValidException      if the cabin is not valid or aliens cannot be placed in starting cabins
     * @throws CabinComponentNotValidException if the alien type is incompatible, already present, or lacks required odd components
     */
    public void setAlien(AlienType newAlien, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Cabin component not valid");
        if (isStarting && newAlien != null)
            throw new ComponentNotValidException("Alien isn't compatible with staring cabin tile");
        else if (this.alien == null && newAlien != null) { // Should set new alien

            // Check if exists an odd component
            this.getLinkedNeighbors(ship).stream()
                    .filter(c -> c.matchesType(OddComponent.class))
                    .map(c -> c.castTo(OddComponent.class))
                    .filter(c -> c.getType() == newAlien)
                    .findFirst()
                    .orElseThrow(() -> new CabinComponentNotValidException("Alien " + newAlien + " is not compatible with this cabin"));

            if (newAlien == AlienType.CANNON && !ship.getCannonAlien()) {
                ship.setCannonAlien(true);
            } else if (newAlien == AlienType.CANNON && ship.getCannonAlien())
                throw new CabinComponentNotValidException("Alien " + newAlien + " is already present in the ship");
            else if (newAlien == AlienType.ENGINE && !ship.getEngineAlien()) {
                ship.setEngineAlien(true);
            } else if (newAlien == AlienType.ENGINE && ship.getEngineAlien())
                throw new CabinComponentNotValidException("Alien " + newAlien + " is already present in the ship");

            setHumans(0, ship);
            ship.setCrew(ship.getCrew() + 1);
        } else if (this.alien != null && newAlien == null) { // Should remove alien
            ship.setCrew(ship.getCrew() - 1);
            if (this.alien == AlienType.CANNON) {
                ship.setCannonAlien(false);
            } else {
                ship.setEngineAlien(false);
            }
        }
        this.alien = newAlien;

        EventContext.emit(new CrewUpdatedEvent(getId(), humans, alien));
    }

    /**
     * Handles the insertion of this cabin component into a player's ship.
     * <p>
     * Starting cabins have special insertion behavior: they are automatically
     * positioned, shown, and welded as part of the ship's initial configuration.
     * Regular cabins follow the standard component insertion process.
     * <p>
     * All cabins contribute their initial crew count (2 humans) to the ship's
     * total crew when inserted, representing the crew quarters being staffed.
     *
     * @param player    the player owning the ship where this component is being inserted
     * @param row       the row position where the component is being placed
     * @param col       the column position where the component is being placed
     * @param rotations the number of rotations applied to the component orientation
     * @param weld      whether the component is being welded permanently to the ship
     */
    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        if (isStarting) {
            this.showComponent();
            this.x = col;
            this.y = row;
            player.getShip().getDashboard()[row][col] = Optional.of(this);
            this.weldComponent();
        } else
            super.insertComponent(player, row, col, rotations, weld);

        player.getShip().setCrew(player.getShip().getCrew() + 2);
    }

    /**
     * Handles the effects of this cabin component being destroyed.
     * <p>
     * When a cabin is destroyed (through damage, meteor impacts, etc.):
     * - All human crew members in the cabin are lost
     * - Any alien specialist in the cabin is lost and ship bonuses are removed
     * - The ship's total crew count is updated accordingly
     * <p>
     * This represents the catastrophic loss of crew quarters and the
     * crew members housed within them during destructive events.
     *
     * @param player the player owning the ship where this component is being destroyed
     */
    @Override
    public void affectDestroy(PlayerData player) {
        setHumans(0, player.getShip());
        setAlien(null, player.getShip());
        super.affectDestroy(player);
    }

    /**
     * Checks if this component matches the specified type.
     * <p>
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * Only returns true for CabinComponent class checks.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if the type matches CabinComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == CabinComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     * <p>
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. Only supports
     * casting to CabinComponent class.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not CabinComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == CabinComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast CabinComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this cabin component.
     * <p>
     * The DTO includes all relevant information about the cabin's current
     * state, including crew composition, alien occupant, starting cabin status,
     * position, and connection information. This is used for client-server
     * communication and UI display purposes.
     *
     * @return a CabinComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new CabinComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), alien, humans, isStarting);
    }

}
