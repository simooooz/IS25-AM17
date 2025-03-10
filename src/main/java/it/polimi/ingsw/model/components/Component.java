package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Component {

    private ConnectorType[] connectors;
    protected int x;
    protected int y;

    public Component(ConnectorType[] connectors) {
        this.connectors = connectors;
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
        ship.getDashboard(y-1, x).ifPresent(n -> {
            if (areConnectorsLinked(connectors[0], n.connectors[2]))
                neighbors.add(n);
        });
        ship.getDashboard(y+1, x).ifPresent(n -> {
            if (areConnectorsLinked(connectors[2], n.connectors[0]))
                neighbors.add(n);
        });
        ship.getDashboard(y, x-1).ifPresent(n -> {
            if (areConnectorsLinked(connectors[3], n.connectors[1]))
                neighbors.add(n);
        });
        ship.getDashboard(y, x+1).ifPresent(n -> {
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

    public void affectDestroy(Ship ship) {
        ship.getDashboard(y, x).ifPresent(ship.getDiscards()::add);
        ship.getDashboard()[y][x] = Optional.empty();
    }

    public void insertComponent(Ship ship, int row, int col) throws Exception {
        if ((row < 0 || row > 4) || (col < 0 || col > 6) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3)) throw new Exception(); // Check if position is valid
        if (ship.getDashboard()[row][col].isPresent()) throw new Exception();

        this.x = row;
        this.y = col;
        ship.getDashboard()[row][col] = Optional.of(this);
    }

    public void checkComponent(Ship ship) throws Exception {
        if ( // Not isolated check
            ship.getDashboard(y, x-1).isEmpty() &&
            ship.getDashboard(y-1, x).isEmpty() &&
            ship.getDashboard(y, x+1).isEmpty() &&
            ship.getDashboard(y+1, x).isEmpty()
        ) throw new Exception();

        if ( // Compatible connectors check
            ship.getDashboard(y, x-1).isPresent() && !Component.areConnectorsCompatible(ship.getDashboard(y, x-1).get().connectors[1], connectors[3]) || // Left connector check
            ship.getDashboard(y-1, x).isPresent() && !Component.areConnectorsCompatible(ship.getDashboard(y-1, x).get().connectors[2], connectors[0]) || // Top connector check
            ship.getDashboard(y, x+1).isPresent() && !Component.areConnectorsCompatible(ship.getDashboard(y, x+1).get().connectors[3], connectors[1]) || // Right connector check
            ship.getDashboard(y+1, x).isPresent() && !Component.areConnectorsCompatible(ship.getDashboard(y+1, x).get().connectors[0], connectors[2]) // Bottom connector check
        ) throw new Exception();
    }

    public void rotateComponent(boolean clockwise) {
        ConnectorType[] newConnectors = new ConnectorType[4];
        if (clockwise) {
            newConnectors[0] = connectors[3];
            newConnectors[1] = connectors[0];
            newConnectors[2] = connectors[1];
            newConnectors[3] = connectors[2];
        }
        else {
            newConnectors[0] = connectors[1];
            newConnectors[1] = connectors[2];
            newConnectors[2] = connectors[3];
            newConnectors[3] = connectors[0];
        }
        connectors = newConnectors;
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

    private void dfs(Optional<Component>[][] dashboard, int i, int j, boolean[][] visited, List<Component> group, Optional<Component> otherComponentOpt) {
        if (i < 0 || i >= dashboard.length || j < 0 || j >= dashboard[0].length) return;
        if (visited[i][j] || dashboard[i][j].isEmpty()) return;

        // Skip if connectors are not linked together
        if (otherComponentOpt.isPresent()) {
            if (
                dashboard[i][j].get().getX() < otherComponentOpt.get().getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[1], otherComponentOpt.get().getConnectors()[3]) ||
                dashboard[i][j].get().getX() > otherComponentOpt.get().getX() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[3], otherComponentOpt.get().getConnectors()[1]) ||
                dashboard[i][j].get().getY() < otherComponentOpt.get().getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[0], otherComponentOpt.get().getConnectors()[2]) ||
                dashboard[i][j].get().getY() > otherComponentOpt.get().getY() && !Component.areConnectorsLinked(dashboard[i][j].get().getConnectors()[2], otherComponentOpt.get().getConnectors()[0])
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
