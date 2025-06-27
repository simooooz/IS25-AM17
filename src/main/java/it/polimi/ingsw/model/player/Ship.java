package it.polimi.ingsw.model.player;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class representing a player's ship in the game.
 * The Ship class manages the placement and organization of components on a grid-based
 * dashboard, along with various ship resources and capabilities.
 *
 * <p>This sealed class serves as the foundation for different ship types:
 * <ul>
 * <li>{@code ShipLearnerMode} - Simplified ship for learning players</li>
 * <li>{@code ShipAdvancedMode} - Enhanced ship with advanced features</li>
 * </ul>
 *
 */
public sealed abstract class Ship permits
        ShipLearnerMode, ShipAdvancedMode {

    /**
     * 2D array representing the ship's component dashboard.
     * Each cell can contain an Optional Component, with empty cells
     * represented by Optional.empty().
     */
    private final Optional<Component>[][] dashboard;

    /**
     * List of components that have been discarded during gameplay.
     * Discarded components are removed from active play but may be
     * referenced for scoring or special abilities.
     */
    private final List<Component> discards;

    /**
     * List of components held in reserve for future placement.
     */
    private final List<Component> reserves;

    /**
     * The component currently held in the player's hand.
     * Only one component can be held at a time, and it represents
     * the component ready for immediate placement or action.
     */
    private Component handComponent;

    /**
     * The current crew count available on this ship.
     */
    private int crew;

    /**
     * The current battery charge level of the ship.
     *
     */
    private int batteries;

    /**
     * Flag indicating whether the ship has alien engine technology.
     */
    private boolean engineAlien;

    /**
     * Flag indicating whether the ship has alien cannon technology.
     */
    private boolean cannonAlien;

    /**
     * Map tracking the quantity of goods of each color type.
     */
    private final Map<ColorType, Integer> goods;

    /**
     * List of ship sides that are protected by shields.
     */
    private final List<DirectionType> protectedSides;

    /**
     * Constructs a new Ship with an empty dashboard and initialized resources.
     * All resource counters start at zero, alien technologies are disabled,
     * and all component collections are empty.
     *
     * <p>The dashboard is initialized as a 2D grid of Optional.empty() values,
     * and goods counters are set to zero for all color types.
     */
    @SuppressWarnings("unchecked")
    public Ship() {
        this.dashboard = new Optional[Constants.SHIP_ROWS][Constants.SHIP_COLUMNS];
        this.discards = new ArrayList<>();
        this.reserves = new ArrayList<>();
        this.handComponent = null;

        this.crew = 0;
        this.batteries = 0;
        this.engineAlien = false;
        this.cannonAlien = false;
        this.protectedSides = new ArrayList<>();
        this.goods = new HashMap<>();
        for (ColorType c : ColorType.values())
            this.goods.put(c, 0);

        for (int row = 0; row < Constants.SHIP_ROWS; row++)
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++)
                this.dashboard[row][col] = Optional.empty();
    }

    /**
     * Returns the complete dashboard as a 2D array of Optional Components.
     *
     * @return the 2D dashboard array where each cell contains an Optional Component
     */
    public Optional<Component>[][] getDashboard() {
        return dashboard;
    }

    /**
     * Returns the component at the specified dashboard position.
     * If the coordinates are out of bounds, returns Optional.empty().
     *
     * @param row the row index (0-based)
     * @param col the column index (0-based)
     * @return Optional containing the component at the position, or empty if
     * no component exists or coordinates are invalid
     */
    public Optional<Component> getDashboard(int row, int col) {
        if (row < 0 || col < 0 || row >= dashboard.length || col >= dashboard[0].length) return Optional.empty();
        return dashboard[row][col];
    }

    /**
     * Returns the list of discarded components.
     * Discarded components are no longer active on the ship but
     * are relevant for final rank.
     *
     * @return a list of discarded components
     */
    public List<Component> getDiscards() {
        return discards;
    }

    /**
     * Returns the list of reserved components.
     * Reserved components are available for future placement
     * but are not currently on the dashboard. They are also
     * relevant for final rank if they are not placed during the game.
     *
     * @return a list of reserved components
     */
    public List<Component> getReserves() {
        return reserves;
    }

    /**
     * Returns the component currently held in the player's hand.
     * The hand can hold at most one component at a time.
     *
     * @return Optional containing the hand component, or empty if no component is held
     */
    public Optional<Component> getHandComponent() {
        return Optional.ofNullable(handComponent);
    }

    /**
     * Sets the component to be held in the player's hand.
     * This replaces any previously held component.
     *
     * @param component the component to place in the hand, or null to empty the hand
     */
    public void setHandComponent(Component component) {
        this.handComponent = component;
    }

    /**
     * Counts the number of exposed connectors across all components on the dashboard.
     * An exposed connector is one that has a connection type other than EMPTY
     * but is not adjacent to another component.
     *
     * @return the total number of exposed connectors on the ship
     */
    public int countExposedConnectors() {
        AtomicInteger exposedConnectors = new AtomicInteger();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> componentOpt : row) {
                componentOpt.ifPresent((Component component) -> {
                    if (component.getConnectors()[0] != ConnectorType.EMPTY && getDashboard(component.getY() - 1, component.getX()).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[1] != ConnectorType.EMPTY && getDashboard(component.getY(), component.getX() + 1).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[2] != ConnectorType.EMPTY && getDashboard(component.getY() + 1, component.getX()).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[3] != ConnectorType.EMPTY && getDashboard(component.getY(), component.getX() - 1).isEmpty())
                        exposedConnectors.getAndIncrement();
                });
            }
        }
        return exposedConnectors.get();
    }

    /**
     * Returns the current crew count on this ship.
     *
     * @return the number of crew members available
     */
    public int getCrew() {
        return crew;
    }

    /**
     * Sets the crew count for this ship.
     *
     * @param crew the new crew count
     */
    public void setCrew(int crew) {
        this.crew = crew;
    }

    /**
     * Returns the current battery charge level.
     *
     * @return the number of battery units available
     */
    public int getBatteries() {
        return batteries;
    }

    /**
     * Sets the battery charge level for this ship.
     *
     * @param batteries the new battery count
     */
    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    /**
     * Checks if this ship has alien engine technology.
     *
     * @return true if alien engine is installed, false otherwise
     */
    public boolean getEngineAlien() {
        return engineAlien;
    }

    /**
     * Checks if this ship has alien cannon technology.
     *
     * @return true if alien cannon is installed, false otherwise
     */
    public boolean getCannonAlien() {
        return cannonAlien;
    }

    /**
     * Sets the alien engine technology status.
     *
     * @param engineAlien true to enable alien engine, false to disable
     */
    public void setEngineAlien(boolean engineAlien) {
        this.engineAlien = engineAlien;
    }

    /**
     * Sets the alien cannon technology status.
     *
     * @param cannonAlien true to enable alien cannon, false to disable
     */
    public void setCannonAlien(boolean cannonAlien) {
        this.cannonAlien = cannonAlien;
    }

    /**
     * Returns the map of goods quantities by color type.
     * The map contains entries for all color types with their current quantities.
     *
     * @return a map where keys are ColorType and values are quantities
     */
    public Map<ColorType, Integer> getGoods() {
        return goods;
    }

    /**
     * Returns the list of protected ship sides.
     *
     * @return a list of DirectionType values representing protected sides
     */
    public List<DirectionType> getProtectedSides() {
        return protectedSides;
    }

    /**
     * Returns all components on the dashboard that match the specified type.
     * This method uses pattern matching to efficiently filter components
     * by their concrete type and safely cast them.
     *
     * @param <T>           the component type to search for
     * @param componentType the Class object representing the desired component type
     * @return a list of components of the specified type found on the dashboard
     */
    public <T extends Component> List<T> getComponentByType(Class<T> componentType) {
        List<T> list = new ArrayList<>();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> component : row) {
                if (component.isPresent()) {
                    Component comp = component.get();
                    switch (comp) {
                        case BatteryComponent bc when componentType.isAssignableFrom(BatteryComponent.class) ->
                                list.add(componentType.cast(bc));
                        case CabinComponent cc when componentType.isAssignableFrom(CabinComponent.class) ->
                                list.add(componentType.cast(cc));
                        case CannonComponent cnc when componentType.isAssignableFrom(CannonComponent.class) ->
                                list.add(componentType.cast(cnc));
                        case EngineComponent ec when componentType.isAssignableFrom(EngineComponent.class) ->
                                list.add(componentType.cast(ec));
                        case OddComponent oc when componentType.isAssignableFrom(OddComponent.class) ->
                                list.add(componentType.cast(oc));
                        case ShieldComponent sc when componentType.isAssignableFrom(ShieldComponent.class) ->
                                list.add(componentType.cast(sc));
                        case CargoHoldsComponent chc when componentType.isAssignableFrom(CargoHoldsComponent.class) ->
                                list.add(componentType.cast(chc));
                        case SpecialCargoHoldsComponent special when componentType.isAssignableFrom(SpecialCargoHoldsComponent.class) ->
                                list.add(componentType.cast(special));
                        case Component c when componentType.isAssignableFrom(Component.class) ->
                                list.add(componentType.cast(c));
                        default -> {
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * Validates the current ship construction.
     * A ship is considered valid if all components pass their individual
     * validation checks, or if the ship has only one component (starting condition).
     *
     * @return true if the ship construction is valid, false otherwise
     */
    public boolean checkShip() {
        boolean valid = true;
        int components = 0;

        for (Optional<Component>[] row : dashboard)
            for (Optional<Component> component : row)
                if (component.isPresent()) {
                    components++;
                    if (!component.get().checkComponent(this))
                        valid = false;
                }
        return (valid && calcShipParts().size()==1) || components == 1;
    }

    /**
     * Calculates and returns all connected component groups on the ship.
     * Components are considered connected if they are adjacent and have
     * compatible connectors linking them together.
     *
     * @return a list where each element is a list of connected components
     */
    public List<List<Component>> calcShipParts() {
        boolean[][] visited = new boolean[dashboard.length][dashboard[0].length];
        List<List<Component>> groups = new ArrayList<>();

        // Find connected groups
        for (int i = 0; i < dashboard.length; i++) {
            for (int j = 0; j < dashboard[0].length; j++) {
                if (this.getDashboard(i, j).isPresent() && !visited[i][j]) {
                    List<Component> group = new ArrayList<>();
                    dfs(i, j, visited, group, null);
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    /**
     * Performs depth-first search to find connected components.
     * This private method recursively explores adjacent components that are
     * connected via compatible connectors.
     *
     * @param i                 the current row index
     * @param j                 the current column index
     * @param visited           2D array tracking which positions have been explored
     * @param group             the current group being built
     * @param otherComponentOpt the component that led to this position (for connector validation)
     */
    @SuppressWarnings({"OptionalGetWithoutIsPresent"})
    private void dfs(int i, int j, boolean[][] visited, List<Component> group, Component otherComponentOpt) {
        if (i < 0 || i >= dashboard.length || j < 0 || j >= dashboard[0].length) return;
        if (visited[i][j] || dashboard[i][j].isEmpty()) return;

        // Skip if connectors are not linked together
        if (otherComponentOpt != null) {
            if (
                    dashboard[i][j].get().getX() < otherComponentOpt.getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[1], otherComponentOpt.getConnectors()[3]) ||
                            dashboard[i][j].get().getX() > otherComponentOpt.getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[3], otherComponentOpt.getConnectors()[1]) ||
                            dashboard[i][j].get().getY() < otherComponentOpt.getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[2], otherComponentOpt.getConnectors()[0]) ||
                            dashboard[i][j].get().getY() > otherComponentOpt.getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[0], otherComponentOpt.getConnectors()[2])
            )
                return;
        }

        visited[i][j] = true;
        dashboard[i][j].ifPresent(group::add);

        // Check close components
        dfs(i - 1, j, visited, group, dashboard[i][j].orElse(null));
        dfs(i + 1, j, visited, group, dashboard[i][j].orElse(null));
        dfs(i, j - 1, visited, group, dashboard[i][j].orElse(null));
        dfs(i, j + 1, visited, group, dashboard[i][j].orElse(null));
    }

    /**
     * Determines if the specified position is valid for component placement.
     * This abstract method must be implemented by concrete ship types to
     * define their specific placement rules.
     *
     * @param row the row position to validate
     * @param col the column position to validate
     * @return true if the position is valid for component placement, false otherwise
     */
    public abstract boolean validPositions(int row, int col);
}
