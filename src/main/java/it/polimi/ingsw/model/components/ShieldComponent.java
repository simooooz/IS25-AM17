package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.dto.ShieldComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.common.model.enums.DirectionType;

/**
 * Component implementation representing a defensive shield system on a player's ship.
 * Shield components provide directional protection against incoming threats such as
 * meteors, cannon fire, and other directional attacks that could damage the ship.
 *
 * Shield systems have several key characteristics:
 * - Directional protection: Each shield protects specific directions (typically two adjacent sides)
 * - Battery-powered activation: Shields require battery power to activate when threats are detected
 * - Rotational adaptation: Shield protection directions rotate with the component orientation
 * - Cumulative coverage: Multiple shields can provide overlapping or comprehensive protection
 *
 * The shield system creates strategic defensive planning opportunities:
 * - Players must balance shield placement to cover vulnerable directions
 * - Shield rotation can adapt protection to face anticipated threats
 * - Battery management becomes critical for shield activation during encounters
 * - Shield destruction removes directional protection, creating defensive vulnerabilities
 *
 * Shield components automatically register their protected directions with the ship's
 * defensive systems, enabling battery-powered deflection of threats from those directions.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public final class ShieldComponent extends Component {

    /** Array of directions that this shield component protects (typically two adjacent directions) */
    private final DirectionType[] directionsProtected;

    /**
     * Constructs a new ShieldComponent with the specified properties.
     * The shield provides protection for the specified directions when activated with battery power.
     *
     * @param id the unique identifier for this shield component
     * @param connectors the array of connector types defining how this component connects to others
     * @param directionsProtected the array of directions this shield protects against threats
     */
    public ShieldComponent(int id, ConnectorType[] connectors, DirectionType[] directionsProtected) {
        super(id, connectors);
        this.directionsProtected = directionsProtected;
    }

    /**
     * Handles the insertion of this shield component into a player's ship with defensive system integration.
     *
     * When a shield component is inserted into the ship:
     * 1. The standard component insertion process is completed
     * 2. The shield's protected directions are registered with the ship's defensive systems
     * 3. The ship gains the ability to use battery power to deflect threats from those directions
     *
     * This integration enables the ship's threat response systems to offer shield activation
     * when attacks come from the protected directions, provided sufficient battery power is available.
     *
     * @param player the player owning the ship where this component is being inserted
     * @param row the row position where the component is being placed
     * @param col the column position where the component is being placed
     * @param rotations the number of rotations applied to the component orientation
     * @param weld whether the component is being welded permanently to the ship
     */
    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        super.insertComponent(player, row, col, rotations, weld);
        for (DirectionType direction : directionsProtected)
            player.getShip().getProtectedSides().add(direction);
    }

    /**
     * Handles shield rotation by updating both component orientation and defensive coverage.
     *
     * When a shield component is rotated:
     * 1. The standard component rotation process is completed
     * 2. The current protection directions are removed from the ship's defensive systems
     * 3. The protection directions are rotated to match the new component orientation
     * 4. The new protection directions are registered with the ship's defensive systems
     *
     * This allows players to dynamically adjust their defensive coverage by rotating
     * shields to face anticipated threats from different directions.
     *
     * @param player the player performing the rotation
     * @param rotations the number of 90-degree clockwise rotations to apply
     */
    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);

        player.getShip().getProtectedSides().remove(directionsProtected[0]);
        player.getShip().getProtectedSides().remove(directionsProtected[1]);

        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + rotations) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + rotations) % 4)];

        player.getShip().getProtectedSides().add(directionsProtected[0]);
        player.getShip().getProtectedSides().add(directionsProtected[1]);
    }

    /**
     * Handles the effects of this shield component being destroyed, including defensive system updates.
     *
     * When a shield component is destroyed:
     * 1. The standard component destruction process is completed
     * 2. The shield's protection directions are removed from the ship's defensive systems
     * 3. The ship loses the ability to deflect threats from those directions
     *
     * This creates immediate defensive vulnerabilities, as attacks from the previously
     * protected directions can no longer be deflected using battery power. Players
     * must rely on other defensive measures or risk component damage from those directions.
     *
     * @param player the player owning the ship where this component is being destroyed
     */
    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        player.getShip().getProtectedSides().remove(directionsProtected[0]);
        player.getShip().getProtectedSides().remove(directionsProtected[1]);
    }

    /**
     * Checks if this component matches the specified type.
     *
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * Only returns true for ShieldComponent class checks.
     *
     * @param type the class type to check against
     * @param <T> the generic type parameter
     * @return true if the type matches ShieldComponent, false otherwise
     */
    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == ShieldComponent.class;
    }

    /**
     * Safely casts this component to the specified type.
     *
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. Only supports
     * casting to ShieldComponent class.
     *
     * @param type the class type to cast to
     * @param <T> the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not ShieldComponent
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == ShieldComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast ShieldComponent to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this shield component.
     *
     * The DTO includes all relevant information about the shield's current
     * state, including its protection directions, position, and connection
     * information. This is used for client-server communication and UI
     * display purposes.
     *
     * @return a ShieldComponentDTO containing this component's current state
     */
    @Override
    public ComponentDTO toDTO() {
        return new ShieldComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), getRotationsCounter(), directionsProtected);
    }

}
