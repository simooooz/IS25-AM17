package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Abstract base class representing a ship component in the game.
 * Components are the building blocks of player ships, providing various capabilities
 * and functionality through a sophisticated connection and placement system.
 * <p>
 * The component system implements several key concepts:
 * - Connector-based linking: Components connect to each other through typed connectors
 * - State management: Components can be picked, reserved, inserted, welded, or destroyed
 * - Event system: All component actions emit events for game state synchronization
 * - Validation: Comprehensive checks ensure valid placement and connector compatibility
 * - Ship integrity: Component destruction can fragment ships requiring player decisions
 * <p>
 * Component lifecycle flows through several states:
 * 1. Hidden/Available: Component exists but not yet acquired by players
 * 2. Picked: Component is in a player's hand for immediate use
 * 3. Reserved: Component is stored for later use (limited capacity)
 * 4. Inserted: Component is placed on the ship but can still be moved/rotated
 * 5. Welded: Component is permanently fixed to the ship
 * 6. Destroyed: Component is removed from the ship (may cause fragmentation)
 * <p>
 * The connector system ensures structural integrity and functional connections
 * between components, while the validation system prevents invalid configurations.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public sealed class Component permits
        BatteryComponent, CabinComponent, CannonComponent,
        EngineComponent, OddComponent,
        ShieldComponent, SpecialCargoHoldsComponent {

    /**
     * The unique identifier for this component
     */
    private final int id;

    /**
     * Array of connector types defining how this component connects to others (North, East, South, West)
     */
    private ConnectorType[] connectors;

    /**
     * The x-coordinate (column) of this component on the ship dashboard
     */
    protected int x;

    /**
     * The y-coordinate (row) of this component on the ship dashboard
     */
    protected int y;

    /**
     * Whether this component has been welded permanently to the ship
     */
    private boolean inserted;

    /**
     * Whether this component has been revealed/shown to players
     */
    private boolean shown;

    private int rotationsCounter;

    /**
     * Constructs a new Component with the specified properties.
     * Initializes the component in a hidden, unwelded state ready for game interaction.
     *
     * @param id         the unique identifier for this component
     * @param connectors the array of connector types (North, East, South, West) defining connection capabilities
     */
    public Component(int id, ConnectorType[] connectors) {
        this.id = id;
        this.connectors = connectors;
        this.inserted = false;
        this.shown = false;
        this.rotationsCounter = 0;
    }

    /**
     * Retrieves the connector types for this component.
     * <p>
     * The connector array defines how this component can connect to adjacent
     * components in the four cardinal directions (North, East, South, West).
     * Connector compatibility determines valid ship configurations.
     *
     * @return array of connector types for the four cardinal directions
     */
    public ConnectorType[] getConnectors() {
        return connectors;
    }

    /**
     * Checks if this component is adjacent to another component.
     * <p>
     * Two components are considered adjacent if they are exactly one grid
     * position apart (Manhattan distance of 1). This is used for proximity
     * checks in various game mechanics like epidemic spread.
     *
     * @param c the other component to check adjacency with
     * @return true if the components are adjacent, false otherwise
     */
    public boolean isNearTo(Component c) {
        int rowDiff = Math.abs(this.x - c.x);
        int colDiff = Math.abs(this.y - c.y);
        return rowDiff + colDiff == 1;
    }

    /**
     * Retrieves all components that are physically connected to this component.
     * <p>
     * Linked neighbors are components that:
     * 1. Are adjacent to this component
     * 2. Have compatible connectors that form functional connections
     * 3. Both connectors are non-empty (actual connections, not just compatibility)
     * <p>
     * This method is crucial for ship connectivity validation and component
     * interaction systems throughout the game.
     *
     * @param ship the ship containing this component and its potential neighbors
     * @return list of components that are functionally linked to this component
     */
    public List<Component> getLinkedNeighbors(Ship ship) {
        List<Component> neighbors = new ArrayList<>();
        ship.getDashboard(y - 1, x).ifPresent(n -> {
            if (areConnectorsLinked(connectors[0], n.connectors[2]))
                neighbors.add(n);
        });
        ship.getDashboard(y + 1, x).ifPresent(n -> {
            if (areConnectorsLinked(connectors[2], n.connectors[0]))
                neighbors.add(n);
        });
        ship.getDashboard(y, x - 1).ifPresent(n -> {
            if (areConnectorsLinked(connectors[3], n.connectors[1]))
                neighbors.add(n);
        });
        ship.getDashboard(y, x + 1).ifPresent(n -> {
            if (areConnectorsLinked(connectors[1], n.connectors[3]))
                neighbors.add(n);
        });
        return neighbors;
    }

    /**
     * Checks if two connector types are compatible for connection.
     * <p>
     * Connector compatibility rules:
     * - Identical types are always compatible
     * - Universal connectors are compatible with any non-empty connector
     * - Empty connectors are only compatible with other empty connectors
     * <p>
     * This allows flexible ship design while maintaining structural logic.
     *
     * @param conn1 the first connector type
     * @param conn2 the second connector type
     * @return true if the connectors can be connected, false otherwise
     */
    public static boolean areConnectorsCompatible(ConnectorType conn1, ConnectorType conn2) {
        if (conn1 == conn2) return true;
        return conn1 == ConnectorType.UNIVERSAL && conn2 != ConnectorType.EMPTY || conn2 == ConnectorType.UNIVERSAL && conn1 != ConnectorType.EMPTY;
    }

    /**
     * Checks if two connector types form an actual functional link.
     * <p>
     * For connectors to be linked, they must be:
     * 1. Compatible according to the compatibility rules
     * 2. Both non-empty (empty connectors don't form functional connections)
     * <p>
     * This distinction separates theoretical compatibility from actual connections.
     *
     * @param conn1 the first connector type
     * @param conn2 the second connector type
     * @return true if the connectors form a functional link, false otherwise
     */
    public static boolean areConnectorsLinked(ConnectorType conn1, ConnectorType conn2) {
        return areConnectorsCompatible(conn1, conn2) && conn1 != ConnectorType.EMPTY && conn2 != ConnectorType.EMPTY;
    }

    /**
     * Handles the effects of this component being destroyed.
     * <p>
     * Component destruction involves:
     * 1. Moving the component to the ship's discard pile
     * 2. Removing it from the ship's dashboard
     * 3. Emitting destruction events for game state updates
     * <p>
     * Subclasses can override this method to handle specific destruction effects
     * like crew loss, battery power reduction, or other component-specific consequences.
     *
     * @param player the player owning the ship where this component is being destroyed
     * @throws ComponentNotValidException if the component is not properly placed on the ship
     */
    public void affectDestroy(PlayerData player) {
        Ship ship = player.getShip();
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Component isn't in dashboard");

        ship.getDiscards().add(this);
        ship.getDashboard()[y][x] = Optional.empty();
        EventContext.emit(new ComponentDestroyedEvent(player.getUsername(), id));
    }

    /**
     * Makes this component visible to players.
     * <p>
     * Shown components can be interacted with by players, while hidden
     * components remain in the game but cannot be picked or manipulated.
     */
    public void showComponent() {
        this.shown = true;
    }

    /**
     * Transfers this component from the common pool to a player's hand.
     * <p>
     * The picking process:
     * 1. Validates the component is available for picking
     * 2. Removes it from the common components or validates it's not reserved
     * 3. Releases any component already in the player's hand
     * 4. Places this component in the player's hand
     * 5. Emits appropriate events for game state updates
     * <p>
     * Players can only hold one component in hand at a time.
     *
     * @param board  the game board containing component pools
     * @param player the player picking up this component
     * @throws ComponentNotValidException if the component cannot be picked
     */
    public void pickComponent(Board board, PlayerData player) {
        Ship ship = player.getShip();
        shown = true;

        if (ship.getReserves().contains(this))
            throw new ComponentNotValidException("Reserves can't be picked");
        else if (board.getCommonComponents().contains(this))
            board.getCommonComponents().remove(this);
        else
            throw new ComponentNotValidException("This component is not in common components or in reserves");

        if (ship.getHandComponent().isPresent())
            ship.getHandComponent().get().releaseComponent(board, player);

        ship.setHandComponent(this);
        EventContext.emit(new ComponentPickedEvent(player.getUsername(), id));
    }

    /**
     * Returns this component to the common pool from the player's control.
     * <p>
     * The release process:
     * 1. Validates the component can be released (not already released, not reserved, not welded)
     * 2. Removes it from the player's hand or dashboard position
     * 3. Returns it to the common components pool
     * 4. Emits appropriate events for game state updates
     * <p>
     * Released components become available for other players to pick.
     *
     * @param board  the game board containing component pools
     * @param player the player releasing this component
     * @throws ComponentNotValidException if the component cannot be released
     */
    public void releaseComponent(Board board, PlayerData player) {
        Ship ship = player.getShip();
        if (board.getCommonComponents().contains(this) || !shown)
            throw new ComponentNotValidException("This component is already released");
        if (ship.getReserves().contains(this))
            throw new ComponentNotValidException("You can't release reserves");
        if (inserted)
            throw new ComponentNotValidException("Component is already welded");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component to release is in hand
            ship.setHandComponent(null);
        else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) // Component to release is in dashboard
            ship.getDashboard()[y][x] = Optional.empty();

        rotationsCounter = 0;
        board.getCommonComponents().add(this);
        EventContext.emit(new ComponentReleasedEvent(player.getUsername(), id));
    }

    /**
     * Moves this component to the player's reserve storage.
     * <p>
     * The reservation process:
     * 1. Validates reserve capacity (maximum 2 components)
     * 2. Removes the component from hand or dashboard
     * 3. Validates the component is not welded (welded components cannot be reserved)
     * 4. Adds it to the reserves and emits events
     * <p>
     * Reserved components are automatically welded when later inserted.
     *
     * @param player the player reserving this component
     * @throws ComponentNotValidException if the component cannot be reserved or reserves are full
     */
    public void reserveComponent(PlayerData player) {
        Ship ship = player.getShip();
        if (ship.getReserves().size() >= 2)
            throw new ComponentNotValidException("Reserves are full");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) { // Component to reserve is in hand
            ship.setHandComponent(null);
        } else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) { // Component to reserve is in dashboard
            if (inserted)
                throw new ComponentNotValidException("Component is already welded");
            ship.getDashboard()[y][x] = Optional.empty();
        } else
            throw new ComponentNotValidException("Component to reserve isn't in hand or in dashboard");

        rotationsCounter = 0;
        ship.getReserves().add(this);
        EventContext.emit(new ComponentReservedEvent(player.getUsername(), id));
    }

    /**
     * Places this component onto the ship's dashboard at the specified position.
     * <p>
     * The insertion process:
     * 1. Validates the target position is available and the component is shown
     * 2. Removes the component from hand or reserves
     * 3. Places it at the specified coordinates on the dashboard
     * 4. Applies any requested rotations
     * 5. Welds the component if required (automatic for reserved components)
     * 6. Emits appropriate events
     * <p>
     * Components from reserves are automatically welded upon insertion.
     *
     * @param player    the player inserting this component
     * @param row       the row position on the ship dashboard
     * @param col       the column position on the ship dashboard
     * @param rotations the number of 90-degree clockwise rotations to apply
     * @param weld      whether to weld the component immediately (ignored for reserved components)
     * @throws ComponentNotValidException if the insertion position or state is invalid
     */
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        Ship ship = player.getShip();

        if (ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("The position where to insert it is not valid"); // Check if new position is valid
        else if (!shown)
            throw new ComponentNotValidException("Component is hidden");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component is in hand
            ship.setHandComponent(null);
        else if (ship.getReserves().contains(this)) { // Component is in reserves, weld it
            ship.getReserves().remove(this);
            weld = true;
        } else
            throw new ComponentNotValidException("Component to insert isn't in hand or in reserves");

        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        rotateComponent(player, rotations);

        // Eventually weld component
        if (weld)
            this.weldComponent();

        EventContext.emit(new ComponentInsertedEvent(player.getUsername(), id, row, col));
    }

    /**
     * Permanently fixes this component to the ship.
     * <p>
     * Welded components cannot be moved, rotated, released, or reserved.
     * This represents the permanent integration of the component into the ship's structure.
     */
    public void weldComponent() {
        inserted = true;
    }

    /**
     * Relocates this component to a new position on the ship dashboard.
     * <p>
     * The movement process:
     * 1. Validates the component is currently on the dashboard and not welded
     * 2. Validates the target position is available
     * 3. Removes the component from its current position
     * 4. Places it at the new coordinates
     * 5. Applies any requested rotations
     * 6. Emits movement events
     * <p>
     * Only unwelded components can be moved.
     *
     * @param player    the player moving this component
     * @param row       the new row position on the ship dashboard
     * @param col       the new column position on the ship dashboard
     * @param rotations the number of 90-degree clockwise rotations to apply
     * @throws ComponentNotValidException if the component cannot be moved or target position is invalid
     */
    public void moveComponent(PlayerData player, int row, int col, int rotations) {
        Ship ship = player.getShip();

        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Component isn't in dashboard");
        else if (inserted)
            throw new ComponentNotValidException("Component is already welded");
        else if (ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("New position isn't valid or is already occupied"); // Check if new position is valid

        ship.getDashboard()[y][x] = Optional.empty();
        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        rotateComponent(player, rotations);
        EventContext.emit(new ComponentMovedEvent(player.getUsername(), id, row, col));
    }

    /**
     * Rotates this component by the specified number of 90-degree increments.
     * <p>
     * The rotation process:
     * 1. Validates the component is accessible (in hand, dashboard, or reserves) and not welded
     * 2. Rotates the connector array clockwise by the specified amount
     * 3. Emits rotation events for game state updates
     * <p>
     * Rotation affects connector positioning, which impacts how the component
     * connects to adjacent components. Subclasses may override this to handle
     * additional rotation effects (like cannon direction updates).
     *
     * @param player    the player rotating this component
     * @param rotations the number of 90-degree clockwise rotations to apply
     * @throws ComponentNotValidException if the component cannot be rotated
     */
    public void rotateComponent(PlayerData player, int rotations) {
        if (rotations % 4 == 0) return;
        Ship ship = player.getShip();
        if ((ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this)) && (ship.getHandComponent().isEmpty() || !ship.getHandComponent().get().equals(this)) && !ship.getReserves().contains(this))
            throw new ComponentNotValidException("Component isn't in hand or in dashboard or in reserves");

        if (inserted)
            throw new ComponentNotValidException("Component is already welded");

        for (int i = 0; i < (rotations % 4); i++) {
            ConnectorType[] newConnectors = new ConnectorType[4];
            newConnectors[0] = connectors[3];
            newConnectors[1] = connectors[0];
            newConnectors[2] = connectors[1];
            newConnectors[3] = connectors[2];
            connectors = newConnectors;
        }

        rotationsCounter = (rotationsCounter + rotations) % 4;

        EventContext.emit(new ComponentRotatedEvent(id, rotations % 4));
    }

    /**
     * Validates that this component can be placed at its current position.
     * <p>
     * The validation process checks:
     * 1. The component is not isolated (has at least one linked neighbor)
     * 2. All adjacent connectors are compatible with neighboring components
     * <p>
     * This ensures ship structural integrity and prevents invalid component
     * configurations. Subclasses can override this method to add specific
     * placement requirements (like cannon firing line clearance).
     *
     * @param ship the ship where this component's placement is being validated
     * @return true if the component can be validly placed, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkComponent(Ship ship) {
        if (getLinkedNeighbors(ship).isEmpty()) // Not isolated check
            return false;

        // Compatible connectors check
        return (ship.getDashboard(y, x - 1).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y, x - 1).get().connectors[1], connectors[3])) && // Left connector check
                (ship.getDashboard(y - 1, x).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y - 1, x).get().connectors[2], connectors[0])) && // Top connector check
                (ship.getDashboard(y, x + 1).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y, x + 1).get().connectors[3], connectors[1])) && // Right connector check
                (ship.getDashboard(y + 1, x).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y + 1, x).get().connectors[0], connectors[2])); // Bottom connector check
    }

    /**
     * Destroys this component and handles potential ship fragmentation.
     * <p>
     * The destruction process:
     * 1. Applies component-specific destruction effects
     * 2. Calculates remaining ship connectivity after component removal
     * 3. If the ship fragments into multiple parts, prompts the player to choose which part to keep
     * 4. Emits ship fragmentation events if necessary
     * <p>
     * Ship fragmentation occurs when component destruction breaks the ship into
     * disconnected sections, requiring players to make strategic decisions about
     * which parts of their ship to preserve.
     *
     * @param player the player owning the ship where this component is being destroyed
     * @return PlayerState.DONE if the ship remains intact, PlayerState.WAIT_SHIP_PART if fragmentation occurred
     */
    public PlayerState destroyComponent(PlayerData player) {
        affectDestroy(player);
        List<List<Component>> groups = player.getShip().calcShipParts();
        if (groups.size() > 1) {
            List<List<Integer>> newGroups = new ArrayList<>();
            for (List<Component> group : groups)
                newGroups.add(group.stream().map(Component::getId).collect(Collectors.toList()));
            EventContext.emit(new ShipBrokenEvent(player.getUsername(), newGroups));
            return PlayerState.WAIT_SHIP_PART;
        }
        return PlayerState.DONE;
    }

    /**
     * Retrieves the x-coordinate (column) of this component.
     *
     * @return the column position on the ship dashboard
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the y-coordinate (row) of this component.
     *
     * @return the row position on the ship dashboard
     */
    public int getY() {
        return y;
    }

    /**
     * Retrieves the unique identifier of this component.
     *
     * @return the component's unique ID
     */
    public int getId() {
        return id;
    }

    /**
     * Checks if this component has been welded to the ship.
     *
     * @return true if the component is permanently attached, false otherwise
     */
    public boolean isInserted() {
        return inserted;
    }

    /**
     * Checks if this component has been revealed to players.
     *
     * @return true if the component is visible and interactive, false otherwise
     */
    public boolean isShown() {
        return shown;
    }

    public int getRotationsCounter() {
        return rotationsCounter;
    }

    /**
     * Checks if this component matches the specified type.
     * <p>
     * This method supports the type-safe component identification system
     * used throughout the game for component filtering and selection.
     * The base implementation handles general Component class checks and
     * assignability relationships.
     *
     * @param type the class type to check against
     * @param <T>  the generic type parameter
     * @return true if this component matches the specified type, false otherwise
     */
    public <T> boolean matchesType(Class<T> type) {
        if (type.isInstance(this)) {
            return true;
        }
        return type == Component.class || type.isAssignableFrom(this.getClass());
    }

    /**
     * Safely casts this component to the specified type.
     * <p>
     * This method provides type-safe casting for component operations
     * that need to work with specific component types. The base implementation
     * handles casting to Component class and assignable types.
     *
     * @param type the class type to cast to
     * @param <T>  the generic type parameter
     * @return this component cast to the specified type
     * @throws ClassCastException if the type is not compatible with this component
     */
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == Component.class || type.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast " + this.getClass().getName() + " to " + type.getName());
    }

    /**
     * Creates a data transfer object representation of this component.
     * <p>
     * The DTO includes basic component information including ID, connectors,
     * position, and state flags. Subclasses typically override this method
     * to include component-specific information in their DTOs.
     *
     * @return a ComponentDTO containing this component's current state
     */
    public ComponentDTO toDTO() {
        return new ComponentDTO(id, connectors, x, y, inserted, shown, rotationsCounter);
    }

}
