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
    private Optional<Component> previousComponent;
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
        this.previousComponent = Optional.empty();
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

    public Optional<Component> getPreviousComponent() {
        return previousComponent;
    }

    public void setPreviousComponent(Component component) {
        this.previousComponent = Optional.ofNullable(component);
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

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            // Arrays to hold each line of the row's representation
            String[] rowLines = new String[5];
            for (int i = 0; i < rowLines.length; i++) {
                rowLines[i] = "";
            }

            // Build each column's representation
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                Optional<Component> componentOpt = dashboard[row][col];
                String[] cellLines;

                if (componentOpt.isPresent()) {
                    // Split the component's string representation into lines
                    cellLines = componentOpt.get().toString().split("\n");
                } else {
                    // Default empty cell representation
                    cellLines = new String[5];
                    cellLines[0] = "┌─────────┐";
                    cellLines[1] = "│         │";
                    cellLines[2] = "│         │";
                    cellLines[3] = "│         │";
                    cellLines[4] = "└─────────┘";
                }

                // Append each cell line to the corresponding row line
                for (int i = 0; i < 5; i++) {
                    rowLines[i] += " " + cellLines[i] + " ";
                }
            }

            // Append all lines for this row to the output
            for (String line : rowLines) {
                output.append(line).append("\n");
            }
        }

        return output.toString();
    }
//    @Override
//    public String toString() {
//        StringBuilder output = new StringBuilder();
//        int rows = Constants.SHIP_ROWS;
//        int cols = Constants.SHIP_COLUMNS;
//
//        // numbers of columns
//        output.append("   ");
//        for (int col = 0; col < cols; col++) {
//            output.append(Chroma.color(String.format("      %-2d     ", col + 4), Chroma.RESET));
//        }
//        output.append("\n");
//
//        // reserves only if standard mode
//        Set<Position> reserves = isLearner
//                ? Set.of()
//                : Set.of(new Position(0, 5), new Position(0, 6));
//
//        // defines colors according to game type (learner o standard)
//        String playableBgColor = isLearner ? Chroma.BLUE_BACKGROUND : Chroma.PURPLE_BACKGROUND;
//        String reserveBgColor = isLearner ? Chroma.DARKBLUE_BACKGROUND : Chroma.DARKPURPLE_BACKGROUND;
//
//        // Matrice per memorizzare le righe di output di ogni componente
//        String[][][] componentsOutput = new String[rows][cols][5]; // [riga][colonna][riga_componente]
//
//        // Prepara tutte le celle (vuote o con componenti)
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < cols; col++) {
//                Position pos = new Position(row, col);
//                boolean isPlayable = validPos().contains(pos);
//                boolean isReserve = reserves.contains(pos);
//                Optional<Component> componentOpt = dashboard[row][col];
//
//                // if a component is placed and shown
//                if (componentOpt.isPresent()) {
//                    String componentOutput = componentOpt.get().print(Optional.empty());
//                    String[] componentLines = componentOutput.split("\n");
//                    // Memorizza le linee nella matrice
//                    for (int i = 0; i < 5; i++) {
//                        componentsOutput[row][col][i] = componentLines[i];
//                    }
//                } else {
//                    // Cella vuota - colora in base al tipo (giocabile, riserva, o normale)
//                    String bgColor = Chroma.RESET;
//                    if (isPlayable) {
//                        bgColor = playableBgColor;
//                    } else if (isReserve) {
//                        bgColor = reserveBgColor;
//                    }
//
//                    // Prepara le linee della cella vuota
//                    String[] emptyCell = new String[5];
//                    emptyCell[0] = " ┌─────────┐ "; // Riga superiore
//                    emptyCell[1] = " │         │ "; // Riga centrale
//                    emptyCell[2] = " │         │ "; // Riga centrale
//                    emptyCell[3] = " │         │ "; // Riga centrale
//                    emptyCell[4] = " └─────────┘ "; // Riga inferiore
//
//                    // Applica colore e memorizza
//                    for (int i = 0; i < 5; i++) {
//                        if (bgColor.equals(Chroma.RESET)) {
//                            componentsOutput[row][col][i] = emptyCell[i];
//                        } else {
//                            componentsOutput[row][col][i] = Chroma.color(emptyCell[i], bgColor);
//                        }
//                    }
//                }
//            }
//        }
//
//        // Costruisci l'output finale riga per riga
//        for (int row = 0; row < rows; row++) {
//            // Per ogni riga del componente (5 righe per componente)
//            for (int componentRow = 0; componentRow < 5; componentRow++) {
//                // Numero di riga mostrato solo nella riga centrale (componentRow = 2)
//                if (componentRow == 2) {
//                    output.append(String.format("%-2d ", row + 5));
//                } else {
//                    output.append("   ");
//                }
//
//                // Stampa la riga corrente di ogni componente in questa riga
//                for (int col = 0; col < cols; col++) {
//                    output.append(componentsOutput[row][col][componentRow]);
//                }
//
//                // Etichetta di riga fine (solo per la riga centrale)
//                if (componentRow == 2) {
//                    output.append(String.format("  %d", row + 5));
//                }
//
//                output.append("\n");
//            }
//        }
//
//        // Stampa intestazione colonne (ripetuta in fondo)
//        output.append("   ");
//        for (int col = 0; col < cols; col++) {
//            output.append(Chroma.color(String.format("      %-2d     ", col + 4), Chroma.RESET));
//        }
//        output.append("\n");
//
//        return output.toString();
//    }


    private static class Position {
        final int row;
        final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private Set<Position> validPos() {
        if (this.isLearner) {
            return Set.of(
                    new Position(4, 1),
                    new Position(4, 2),
                    new Position(4, 4),
                    new Position(4, 5),

                    // row 1
                    new Position(3, 1),
                    new Position(3, 2),
                    new Position(3, 3),
                    new Position(3, 4),
                    new Position(3, 5),

                    // row 2
                    new Position(2, 1),
                    new Position(2, 2),
                    new Position(2, 3),
                    new Position(2, 4),
                    new Position(2, 5),

                    // row 3
                    new Position(1, 2),
                    new Position(1, 3),
                    new Position(1, 4),

                    // row 4
                    new Position(0, 3)
            );
        } else {
            return Set.of(
                    // row 0
                    new Position(4, 0),
                    new Position(4, 1),
                    new Position(4, 2),
                    new Position(4, 4),
                    new Position(4, 5),
                    new Position(4, 6),

                    // row 1
                    new Position(3, 0),
                    new Position(3, 1),
                    new Position(3, 2),
                    new Position(3, 3),
                    new Position(3, 4),
                    new Position(3, 5),
                    new Position(3, 6),

                    // row 2
                    new Position(2, 0),
                    new Position(2, 1),
                    new Position(2, 2),
                    new Position(2, 3),
                    new Position(2, 4),
                    new Position(2, 5),
                    new Position(2, 6),

                    // row 3
                    new Position(1, 1),
                    new Position(1, 2),
                    new Position(1, 3),
                    new Position(1, 4),
                    new Position(1, 5),

                    // row 4
                    new Position(0, 2),
                    new Position(0, 4)
            );
        }
    }

}
