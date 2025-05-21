package it.polimi.ingsw.model.player;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.properties.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ship {
    private final Optional<Component>[][] dashboard;
    private final boolean isLearner;
    private final List<Component> discards;
    private final List<Component> reserves;
    private Optional<Component> handComponent;
    private int crew;
    private int batteries;
    private boolean engineAlien;
    private boolean cannonAlien;
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

    public Ship(boolean isLearner) {
        this.dashboard = new Optional[Constants.SHIP_ROWS][Constants.SHIP_COLUMNS];
        this.discards = new ArrayList<>();
        this.reserves = new ArrayList<>();
        this.handComponent = Optional.empty();
        this.crew = 0;
        this.batteries = 0;
        this.engineAlien = false;
        this.cannonAlien = false;
        this.goods = new HashMap<>();
        for (ColorType c : ColorType.values()) {
            this.goods.put(c, 0);
        }
        this.protectedSides = new ArrayList<>();
        this.isLearner = isLearner;

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                this.dashboard[row][col] = Optional.empty();
            }
        }
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
        return handComponent;
    }

    public void setHandComponent(Component component) {
        this.handComponent = Optional.ofNullable(component);
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
                if (component.isPresent() && componentType.isInstance(component.get())) {
                    list.add(componentType.cast(component.get()));
                }
            }
        }
        return list;
    }

    public boolean checkShip() {
        for (Optional<Component>[] row : dashboard)
            for (Optional<Component> component : row)
                if (component.isPresent() && !component.get().checkComponent(this))
                    return false;
        return true;
    }

    public List<List<Component>> calcShipParts() {

        // Matrix of booleans to track visited components
        boolean[][] visited = new boolean[dashboard.length][dashboard[0].length];
        List<List<Component>> groups = new ArrayList<>();

        // Find connected groups
        for (int i = 0; i < dashboard.length; i++) {
            for (int j = 0; j < dashboard[0].length; j++) {
                if (this.getDashboard(i, j).isPresent() && !visited[i][j]) {
                    List<Component> group = new ArrayList<>();
                    dfs(i, j, visited, group, Optional.empty());
                    groups.add(group);
                }
            }
        }

        return groups;
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "OptionalUsedAsFieldOrParameterType"})
    private void dfs(int i, int j, boolean[][] visited, List<Component> group, Optional<Component> otherComponentOpt) {
        if (i < 0 || i >= dashboard.length || j < 0 || j >= dashboard[0].length) return;
        if (visited[i][j] || dashboard[i][j].isEmpty()) return;

        // Skip if connectors are not linked together
        if (otherComponentOpt.isPresent()) {
            if (
                    dashboard[i][j].get().getX() < otherComponentOpt.get().getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[1], otherComponentOpt.get().getConnectors()[3]) ||
                            dashboard[i][j].get().getX() > otherComponentOpt.get().getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[3], otherComponentOpt.get().getConnectors()[1]) ||
                            dashboard[i][j].get().getY() < otherComponentOpt.get().getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[2], otherComponentOpt.get().getConnectors()[0]) ||
                            dashboard[i][j].get().getY() > otherComponentOpt.get().getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[0], otherComponentOpt.get().getConnectors()[2])
            )
                return;
        }

        visited[i][j] = true;
        dashboard[i][j].ifPresent(group::add);

        // Check close components
        dfs(i - 1, j, visited, group, dashboard[i][j]);
        dfs(i + 1, j, visited, group, dashboard[i][j]);
        dfs(i, j - 1, visited, group, dashboard[i][j]);
        dfs(i, j + 1, visited, group, dashboard[i][j]);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();

        output.append("   ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++) // Column label
            output.append(Chroma.color(String.format("       %-2d      ", col + 4), Chroma.RESET));
        output.append("\n");

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int componentRow = 0; componentRow < 5; componentRow++) {

                if (componentRow == 2) // Row label
                    output.append((row + 5)).append("  ");
                else
                    output.append("   ");

                for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                    Optional<Component> componentOpt = getDashboard(row, col);

                    boolean isPlayable = Component.validPositions(row, col, isLearner);
                    if (componentOpt.isPresent()) {
                        String[] cellLines = componentOpt.get().toString().split("\n");
                        output.append(" ").append(cellLines[componentRow]).append(" ");
                    }
                    else {
                        String bgColor;
                        if (isPlayable)
                            bgColor = isLearner ? Chroma.BLUE_BACKGROUND : Chroma.PURPLE_BACKGROUND;
                        else
                            bgColor = Chroma.RESET;

                        if (componentRow == 0)
                            output.append(Chroma.color("  ┌─────────┐  ", bgColor));
                        else if (componentRow == 4)
                            output.append(Chroma.color("  └─────────┘  ", bgColor));
                        else
                            output.append(Chroma.color("  │         │  ", bgColor));
                    }

                }

                if (componentRow == 2) // Row label
                    output.append("  ").append(row + 5);

                output.append("\n");
            }
        }

        output.append("   ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++) // Column label
            output.append(String.format("       %-2d      ", col + 4));
        output.append("\n");

        return output.toString();
    }

}
