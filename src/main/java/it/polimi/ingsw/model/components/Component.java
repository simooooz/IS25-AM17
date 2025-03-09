package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class Component {

    private ConnectorType[] connectors;
    private int x;
    private int y;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

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

    public void insertComponent(Ship ship, int row, int col) throws Exception {
        if ((row < 0 || row > 4) || (col < 0 || col > 6) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3))
            throw new Exception(); // Check if position is valid
        if (ship.getDashboard()[row][col].isPresent()) throw new Exception();

        if ( // Not isolated check
                !(col > 0 && ship.getDashboard()[row][col - 1].isPresent()) &&
                        !(row > 0 && ship.getDashboard()[row - 1][col].isPresent()) &&
                        !(col < 6 && ship.getDashboard()[row][col + 1].isPresent()) &&
                        !(row < 4 && ship.getDashboard()[row + 1][col].isPresent())
        ) throw new Exception();
        if ( // Left connectors check
                col > 0 && ship.getDashboard()[row][col - 1].isPresent() &&
                        ship.getDashboard()[row][col - 1].get().connectors[1] != ship.getDashboard()[row][col].get().connectors[3] &&
                        ((ship.getDashboard()[row][col - 1].get().connectors[1] != ConnectorType.UNIVERSAL && ship.getDashboard()[row][col].get().connectors[3] != ConnectorType.UNIVERSAL) ||
                                (ship.getDashboard()[row][col - 1].get().connectors[1] == ConnectorType.EMPTY || ship.getDashboard()[row][col].get().connectors[3] == ConnectorType.EMPTY))
        ) throw new Exception();
        if ( // Top connectors check
                row > 0 && ship.getDashboard()[row - 1][col].isPresent() &&
                        ship.getDashboard()[row - 1][col].get().connectors[2] != ship.getDashboard()[row][col].get().connectors[0] &&
                        ((ship.getDashboard()[row - 1][col].get().connectors[2] != ConnectorType.UNIVERSAL && ship.getDashboard()[row][col].get().connectors[0] != ConnectorType.UNIVERSAL) ||
                                (ship.getDashboard()[row - 1][col].get().connectors[2] == ConnectorType.EMPTY || ship.getDashboard()[row][col].get().connectors[0] == ConnectorType.EMPTY))
        ) throw new Exception();
        if ( // Right connectors check
                col < 6 && ship.getDashboard()[row][col + 1].isPresent() &&
                        ship.getDashboard()[row][col + 1].get().connectors[3] != ship.getDashboard()[row][col].get().connectors[1] &&
                        ((ship.getDashboard()[row][col + 1].get().connectors[3] != ConnectorType.UNIVERSAL && ship.getDashboard()[row][col].get().connectors[1] != ConnectorType.UNIVERSAL) ||
                                (ship.getDashboard()[row][col + 1].get().connectors[3] == ConnectorType.EMPTY || ship.getDashboard()[row][col].get().connectors[1] == ConnectorType.EMPTY))
        ) throw new Exception();
        if ( // Bottom connectors check
                row < 4 && ship.getDashboard()[row + 1][col].isPresent() &&
                        ship.getDashboard()[row + 1][col].get().connectors[0] != ship.getDashboard()[row][col].get().connectors[2] &&
                        ((ship.getDashboard()[row + 1][col].get().connectors[0] != ConnectorType.UNIVERSAL && ship.getDashboard()[row][col].get().connectors[2] != ConnectorType.UNIVERSAL) ||
                                (ship.getDashboard()[row + 1][col].get().connectors[0] == ConnectorType.EMPTY || ship.getDashboard()[row][col].get().connectors[2] == ConnectorType.EMPTY))
        ) throw new Exception();

        this.x = row;
        this.y = col;
        ship.getDashboard()[row][col] = Optional.of(this);
    }

    public void rotateComponent(boolean clockwise) {
        ConnectorType[] newConnectors = new ConnectorType[4];
        if (clockwise) {
            newConnectors[0] = connectors[3];
            newConnectors[1] = connectors[0];
            newConnectors[2] = connectors[1];
            newConnectors[3] = connectors[2];
        } else {
            newConnectors[0] = connectors[1];
            newConnectors[1] = connectors[2];
            newConnectors[2] = connectors[3];
            newConnectors[3] = connectors[0];
        }
        connectors = newConnectors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
