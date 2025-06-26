package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.dto.OddComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

/**
 * Component implementation representing specialized alien support infrastructure on a player's ship.
 * Odd components serve as essential support systems that enable alien crew members to
 * provide their specialized bonuses to the ship's operations.
 *
 * The odd component system enforces alien placement requirements:
 * - Alien crew can only be placed in cabins that are adjacent to compatible odd components
 * - Each alien type (cannon or engine) requires its corresponding odd component type
 * - Multiple odd components of the same type can support the same alien crew member
 * - Destruction of all supporting odd components forces the removal of the alien crew
 *
 * This system creates strategic depth in ship design, as players must plan both
 * crew quarters and support infrastructure to maximize alien crew effectiveness.
 * The requirement for compatible odd components represents the specialized equipment
 * and facilities that alien crew members need to perform their enhanced functions.
 *
 * Odd components also create vulnerability points: losing critical support infrastructure
 * can force the removal of valuable alien crew members, making ship design and
 * component protection strategically important.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public final class OddComponent extends Component {

    /** The type of alien this odd component supports (cannon or engine) */
    private final AlienType type;

    /**
     * Constructs a new OddComponent with the specified properties.
     * The odd component is specialized for supporting a specific type of alien crew member.
     *
     * @param id the unique identifier for this odd component
     * @param connectors the array of connector types defining how this component connects to others
     * @param type the type of alien this component supports (cannon or engine)
     */
    public OddComponent(int id, ConnectorType[] connectors, AlienType type) {
        super(id, connectors);
        this.type = type;
    }

    /**
     * Retrieves the type of alien this odd component supports.
     *
     * The alien type determines:
     * - Which alien crew members can be placed in adjacent cabins
     * - What specialized functionality this component enables
     * - Which other odd components can provide redundant support
     *
     * @return the AlienType indicating which alien specialization this component supports
     */
    public AlienType getType() {
        return type;
    }

    /**
     * Handles the effects of this odd component being destroyed, including alien crew management.
     *
     * When an odd component is destroyed, the method performs complex alien support validation:
     * 1. Calls the parent destruction logic to handle basic component removal
     * 2. Checks if there are any alien crew of the supported type currently on the ship
     * 3. Identifies any linked cabin containing an alien of the supported type
     * 4. Searches for alternative odd components that could support the same alien
     * 5. If no alternative support exists, forces the removal of the alien crew member
     *
     * This system ensures that alien crew members can only remain on the ship while
     * they have appropriate support infrastructure. Destroying critical odd components
     * can force the loss of valuable alien specialists, creating strategic vulnerabilities.
     *
     * @param player the player owning the ship where this component is being destroyed
     */
    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        Ship ship = player.getShip();

        if (type == AlienType.CANNON && !ship.getCannonAlien() || type == AlienType.ENGINE && !ship.getEngineAlien())
            return;

        Optional<CabinComponent> linkedCabin = this.getLinkedNeighbors(ship).stream()
                .filter(c -> c.matchesType(CabinComponent.class))
                .map(c -> c.castTo(CabinComponent.class))
                .filter(c -> c.getAlien().isPresent() && c.getAlien().get() == type)
                .findFirst();

        if (linkedCabin.isPresent()) {
            Optional<OddComponent> anotherOdd = linkedCabin.get().getLinkedNeighbors(ship).stream()
                    .filter(c -> c.matchesType(OddComponent.class))
                    .map(c -> c.castTo(OddComponent.class))
                    .filter(o -> o.getType() == type && !o.equals(this))
                    .findFirst();

            if (anotherOdd.isEmpty()) // Remove alien in linkedCabin
                linkedCabin.get().setAlien(null, ship);
        }
    }

    /**
     * Checks if this component matches the specified type.
     *
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * Only returns true for OddComponent class checks.
     *
     * @param type the class type to check against
     * @param <T> the generic type parameter
     * @return true if the type matches OddComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == OddComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     *
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. Only supports
     * casting to OddComponent class.
     *
     * @param type the class type to cast to
     * @param <T> the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not OddComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == OddComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast OddComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this odd component.
     *
     * The DTO includes all relevant information about the odd component's
     * current state, including its alien support type, position, and connection
     * information. This is used for client-server communication and UI
     * display purposes.
     *
     * @return an OddComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new OddComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), type);
    }

}
