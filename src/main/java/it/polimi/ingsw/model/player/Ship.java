package it.polimi.ingsw.model.player;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public sealed abstract class Ship permits
    ShipLearnerMode, ShipAdvancedMode
{

    private final Optional<Component>[][] dashboard;
    private final List<Component> discards;
    private final List<Component> reserves;
    private Component handComponent;

    private int crew;
    private int batteries;
    private boolean engineAlien;
    private boolean cannonAlien;
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

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

    public Optional<Component>[][] getDashboard() {
        return dashboard;
    }

    public Optional<Component> getDashboard(int row, int col) {
        if (row < 0 || col < 0 || row >= dashboard.length || col >= dashboard[0].length) return Optional.empty();
        return dashboard[row][col];
    }

    public List<Component> getDiscards() {
        return discards;
    }

    public List<Component> getReserves() {
        return reserves;
    }

    /**
     * @return the component in hand
     */
    public Optional<Component> getHandComponent() {
        return Optional.ofNullable(handComponent);
    }

    public void setHandComponent(Component component) {
        this.handComponent = component;
    }

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

    public int getCrew() {
        return crew;
    }

    public void setCrew(int crew) {
        this.crew = crew;
    }

    public int getBatteries() {
        return batteries;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    public boolean getEngineAlien() {
        return engineAlien;
    }

    public boolean getCannonAlien() {
        return cannonAlien;
    }

    public void setEngineAlien(boolean engineAlien) {
        this.engineAlien = engineAlien;
    }

    public void setCannonAlien(boolean cannonAlien) {
        this.cannonAlien = cannonAlien;
    }

    public Map<ColorType, Integer> getGoods() {
        return goods;
    }

    public List<DirectionType> getProtectedSides() {
        return protectedSides;
    }

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
                        default -> {}
                    }
                }
            }
        }
        return list;
    }

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

        return valid || components == 1;
    }

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

    public abstract boolean validPositions(int row, int col);

}
