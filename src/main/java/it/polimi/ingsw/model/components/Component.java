package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Component {

    private ConnectorType[] connectors;
    protected int x;
    protected int y;
    private boolean inserted;
    private boolean shown;

    public Component(ConnectorType[] connectors) {
        this.connectors = connectors;
        this.inserted = false;
        this.shown = false;
    }

    public ConnectorType[] getConnectors() {
        return connectors;
    }

    public boolean isNearTo(Component c) {
        int rowDiff = Math.abs(this.x - c.x);
        int colDiff = Math.abs(this.y - c.y);
        return rowDiff + colDiff == 1;
    }

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

    public static boolean areConnectorsCompatible(ConnectorType conn1, ConnectorType conn2) {
        if (conn1 == conn2) return true;
        return conn1 == ConnectorType.UNIVERSAL && conn2 != ConnectorType.EMPTY || conn2 == ConnectorType.UNIVERSAL && conn1 != ConnectorType.EMPTY;
    }

    public static boolean areConnectorsLinked(ConnectorType conn1, ConnectorType conn2) {
        return areConnectorsCompatible(conn1, conn2) && conn1 != ConnectorType.EMPTY && conn2 != ConnectorType.EMPTY;
    }

    public static boolean validPositions(int row, int col) {
        return !((col < 0 || col > 6) || (row < 0 || row > 5) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3));
    }

    public void affectDestroy(Ship ship) {
        ship.getDashboard(y, x).ifPresent(ship.getDiscards()::add);
        ship.getDashboard()[y][x] = Optional.empty();
    }

    /**
     * Sets the shown attribute of this(component) to true
     */
    public void showComponent() {
        this.shown = true;
    }

    /**
     * Handles the selection of this(component) by a ship from the
     * board's list with the components available
     *
     * @param board Board ref
     * @param ship  Ship ref
     * @throws ComponentNotValidException if the component is not pickable
     */
    public void pickComponent(Board board, Ship ship) {
        if (!board.getCommonComponents().contains(this) || !shown)
            throw new ComponentNotValidException("This component is not pickable");

        if (ship.getHandComponent().isPresent())
            ship.getHandComponent().get().releaseComponent(board, ship);

        board.getCommonComponents().remove(this);
        ship.setHandComponent(this);
    }

    /**
     * Releases to the board this(component) from the hand, or ship if not inserted yet
     *
     * @param board
     * @param ship
     */
    public void releaseComponent(Board board, Ship ship) {
        if (board.getCommonComponents().contains(this) || inserted || !shown)
            throw new ComponentNotValidException("This component is not releaseble");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) { // Component to release is in hand
            ship.setHandComponent(null);
            board.getCommonComponents().add(this);
        } else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) { // Component to release is in dashboard
            ship.getDashboard()[y][x] = Optional.empty();
            board.getCommonComponents().add(this);
            ship.setPreviousComponent(null);
        }
    }

    public void reserveComponent(Board board, Ship ship) {
        if (!board.getCommonComponents().contains(this) || !shown)
            throw new ComponentNotValidException("This component is not reservable");
        if (ship.getReserves().size() >= 2) throw new ComponentNotValidException("Reserves are full");

        board.getCommonComponents().remove(this);
        ship.getReserves().add(this);
    }

    public void insertComponent(Ship ship, int row, int col) {
        if (!Component.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("Position not valid"); // Check if new position is valid
        else if (!shown) throw new ComponentNotValidException("Hidden tile");

        if (ship.getReserves().contains(this)) // Component is into reserves
            ship.getReserves().remove(this);
        else if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component is in hand
            ship.setHandComponent(null);
        else
            throw new ComponentNotValidException("Tile to insert isn't present");

        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        // Weld previous component and update attribute to new component
        ship.getPreviousComponent().ifPresent(Component::weldComponent);
        ship.setPreviousComponent(this);
    }

    public void weldComponent() {
        inserted = true;
    }

    public void moveComponent(Ship ship, int row, int col) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");
        if (inserted || !shown) throw new ComponentNotValidException("Tile already welded or hidden");
        if (!Component.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("Position not valid"); // Check if new position is valid

        ship.getDashboard()[y][x] = Optional.empty();
        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);
    }

    public void rotateComponent(Ship ship) {
        if (inserted || !shown) throw new ComponentNotValidException("Tile already welded or hidden");
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");

        ConnectorType[] newConnectors = new ConnectorType[4];
        newConnectors[0] = connectors[3];
        newConnectors[1] = connectors[0];
        newConnectors[2] = connectors[1];
        newConnectors[3] = connectors[2];
        connectors = newConnectors;
    }

    public boolean checkComponent(Ship ship) {
        if (getLinkedNeighbors(ship).isEmpty()) // Not isolated check
            return false;

        // Compatible connectors check
        return (ship.getDashboard(y, x - 1).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y, x - 1).get().connectors[1], connectors[3])) && // Left connector check
                (ship.getDashboard(y - 1, x).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y - 1, x).get().connectors[2], connectors[0])) && // Top connector check
                (ship.getDashboard(y, x + 1).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y, x + 1).get().connectors[3], connectors[1])) && // Right connector check
                (ship.getDashboard(y + 1, x).isEmpty() || Component.areConnectorsCompatible(ship.getDashboard(y + 1, x).get().connectors[0], connectors[2])); // Bottom connector check
    }

    public void destroyComponent(Ship ship) {
        affectDestroy(ship);

        // Matrix of booleans to track visited components
        boolean[][] visited = new boolean[ship.getDashboard().length][ship.getDashboard()[0].length];
        List<List<Component>> groups = new ArrayList<>();

        // Find connected groups
        for (int i = 0; i < ship.getDashboard().length; i++) {
            for (int j = 0; j < ship.getDashboard()[0].length; j++) {
                if (ship.getDashboard(i, j).isPresent() && !visited[i][j]) {
                    List<Component> group = new ArrayList<>();
                    dfs(ship.getDashboard(), i, j, visited, group, Optional.empty());
                    groups.add(group);
                }
            }
        }

        if (groups.size() > 1) { // TODO (or have no more cabins)
            int toKeep = 0; // View
            for (int i = 0; i < groups.size(); i++) {
                if (i != toKeep) {
                    for (Component componentToRemove : groups.get(i)) {
                        componentToRemove.affectDestroy(ship);
                    }
                }
            }
        }
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "OptionalUsedAsFieldOrParameterType"})
    private void dfs(Optional<Component>[][] dashboard, int i, int j, boolean[][] visited, List<Component> group, Optional<Component> otherComponentOpt) {
        if (i < 0 || i >= dashboard.length || j < 0 || j >= dashboard[0].length) return;
        if (visited[i][j] || dashboard[i][j].isEmpty()) return;

        // Skip if connectors are not linked together
        if (otherComponentOpt.isPresent()) {
            if (
                    dashboard[i][j].get().x < otherComponentOpt.get().x && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[1], otherComponentOpt.get().getConnectors()[3]) ||
                            dashboard[i][j].get().x > otherComponentOpt.get().x && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[3], otherComponentOpt.get().getConnectors()[1]) ||
                            dashboard[i][j].get().y < otherComponentOpt.get().y && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[2], otherComponentOpt.get().getConnectors()[0]) ||
                            dashboard[i][j].get().y > otherComponentOpt.get().y && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[0], otherComponentOpt.get().getConnectors()[2])
            )
                return;
        }

        visited[i][j] = true;
        dashboard[i][j].ifPresent(group::add);

        // Check close components
        dfs(dashboard, i - 1, j, visited, group, dashboard[i][j]);
        dfs(dashboard, i + 1, j, visited, group, dashboard[i][j]);
        dfs(dashboard, i, j - 1, visited, group, dashboard[i][j]);
        dfs(dashboard, i, j + 1, visited, group, dashboard[i][j]);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
