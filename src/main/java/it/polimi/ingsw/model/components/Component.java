package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.Constants;

import static it.polimi.ingsw.Constants.inTheMiddle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Component {

    private final int id;
    private ConnectorType[] connectors;
    protected int x;
    protected int y;
    private boolean inserted;
    private boolean shown;

    public Component(int id, ConnectorType[] connectors) {
        this.id = id;
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

    public static boolean validPositions(int row, int col, boolean learnerMode) {
        if (learnerMode)
            return !((col < 1 || col > 5) || (row < 0 || row > 5) || (row == 0 && col == 1) || (row == 0 && col == 2) || (row == 0 && col == 4) || (row == 0 && col == 5) || (row == 1 && col == 1) || (row == 1 && col == 5) || (row == 4 && col == 3));
        return !((col < 0 || col > 6) || (row < 0 || row > 5) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3));
    }

    public void affectDestroy(Ship ship) {
        ship.getDashboard(y, x).ifPresent(ship.getDiscards()::add);
        ship.getDashboard()[y][x] = Optional.empty();
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
        shown = true;

        if (ship.getReserves().contains(this))
            throw new ComponentNotValidException("Reserves can't be picked");
        else if (board.getCommonComponents().contains(this))
            board.getCommonComponents().remove(this);
        else
            throw new ComponentNotValidException("This component is not in common components or in reserves");

        if (ship.getHandComponent().isPresent())
            ship.getHandComponent().get().releaseComponent(board, ship);

        ship.setHandComponent(this);
    }

    /**
     * Releases to the board this(component) from the hand, or ship if not welded yet
     *
     * @param board
     * @param ship
     */
    public void releaseComponent(Board board, Ship ship) {
        if (board.getCommonComponents().contains(this) || !shown)
            throw new ComponentNotValidException("This component is already released");
        if (inserted)
            throw new ComponentNotValidException("Component is welded");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) { // Component to release is in hand
            ship.setHandComponent(null);
            board.getCommonComponents().add(this);
        } else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) { // Component to release is in dashboard
            ship.getDashboard()[y][x] = Optional.empty();
            board.getCommonComponents().add(this);
            ship.setPreviousComponent(null);
        }
    }

    public void reserveComponent(Ship ship) {
        if (ship.getReserves().size() >= 2)
            throw new ComponentNotValidException("Reserves are full");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) {
            ship.setHandComponent(null);
        }
        else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) { // Component to reserve is in dashboard
            if (inserted)
                throw new ComponentNotValidException("Component is already welded");
            ship.getDashboard()[y][x] = Optional.empty();
            ship.setPreviousComponent(null);
        }
        else
            throw new ComponentNotValidException("Component to reserve isn't in hand or in dashboard");

        ship.getReserves().add(this);
    }

    public void insertComponent(Ship ship, int row, int col, int rotations, boolean learnerMode) {
        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component is in hand
            ship.setHandComponent(null);
        else if (ship.getReserves().contains(this))
            ship.getReserves().remove(this);
        else
            throw new ComponentNotValidException("Component to insert isn't in hand or in reserves");

        if (!Component.validPositions(row, col, learnerMode) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("The position where to insert it is not valid"); // Check if new position is valid
        else if (!shown) throw new ComponentNotValidException("Component is hidden");

        for (int i=0; i<rotations; i++)
            rotateComponent(ship);

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

    public void moveComponent(Ship ship, int row, int col, boolean learnerMode) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Component isn't in dashboard");
        else if (inserted)
            throw new ComponentNotValidException("Component already welded");
        else if (!Component.validPositions(row, col, learnerMode) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("New position isn't valid or is already occupied"); // Check if new position is valid

        ship.getDashboard()[y][x] = Optional.empty();
        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);
    }

    public void rotateComponent(Ship ship) {
        if ((ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this)) && (ship.getHandComponent().isEmpty() || !ship.getHandComponent().get().equals(this)))
            throw new ComponentNotValidException("Component isn't in hand or in dashboard");
        if (inserted)
            throw new ComponentNotValidException("Component is already welded");

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

    // Returns DONE if there is only a part, otherwise WAIT_SHIP_PART
    public PlayerState destroyComponent(Ship ship) {
        affectDestroy(ship);
        List<List<Component>> groups = ship.calcShipParts();

        if (groups.size() > 1)
            return PlayerState.WAIT_SHIP_PART;
        return PlayerState.DONE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};

        List<String> componentLines = new ArrayList<>();
        if (!shown) {
            String topBorder = " " + angles[0] + Constants.repeat(hBorder, 9) + angles[1] + " ";
            componentLines.add(topBorder);

            String leftBorder = "";
            leftBorder = " " + vBorder +
                    (String.valueOf(id).length() == 2 ? "   " + "\u2004" + this.id + "\u2004" + "   "
                    : inTheMiddle(String.valueOf(this.id), 9)) + vBorder + " ";
            componentLines.add(leftBorder);

            leftBorder = " " + vBorder + Constants.repeat(" ", 9) + vBorder + " ";
            componentLines.add(leftBorder);

            leftBorder = " " + vBorder + Constants.repeat(" ", 9) + vBorder + " ";
            componentLines.add(leftBorder);

            String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 9) + angles[3] + " ";
            componentLines.add(bottomBorder);

            return String.join("\n", componentLines);
        }
        else {
            // First row
            String topBorder = " " + angles[0];

            if (connectors[0] == ConnectorType.EMPTY)
                topBorder = topBorder + Constants.repeat(hBorder, 9) + angles[1] + " ";
            else if (connectors[0]==ConnectorType.SINGLE)
                topBorder = topBorder + Constants.repeat(hBorder, 4) + "┴" + Constants.repeat(hBorder, 4) + angles[1] + " ";
            else if (connectors[0]==ConnectorType.DOUBLE)
                topBorder = topBorder + hBorder + "┴" + Constants.repeat(hBorder, 5) + "┴" + hBorder + angles[1] + " ";
            else if (connectors[0]==ConnectorType.UNIVERSAL)
                topBorder = topBorder + hBorder + "┴" + Constants.repeat(hBorder, 2) + "┴" + Constants.repeat(hBorder, 2) + "┴" + hBorder + angles[1] + " ";
            componentLines.add(topBorder);

            // Second Riga
            String leftBorder = "";
            switch (connectors[3]) {
                case ConnectorType.EMPTY, ConnectorType.SINGLE -> {
                    leftBorder = " " + vBorder;
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder +
                                (String.valueOf(id).length() == 2 ? "   " + "\u2004" + this.id + "\u2004" + "   "
                                : inTheMiddle(String.valueOf(this.id), 9)) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder +
                                (String.valueOf(id).length() == 2 ? "   " + "\u2004" + this.id + "\u2004" + "   "
                                : inTheMiddle(String.valueOf(this.id), 9)) + "├" + hBorder;
                    }
                }
                case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> {
                    leftBorder = hBorder + "┤";
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder +
                                (String.valueOf(id).length() == 2 ? "   " + "\u2004" + this.id + "\u2004" + "   "
                                : inTheMiddle(String.valueOf(this.id), 9)) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder +
                                (String.valueOf(id).length() == 2 ? "   " + "\u2004" + this.id + "\u2004" + "   "
                                : inTheMiddle(String.valueOf(this.id), 9)) + "├" + hBorder;
                    }
                }
            }
            componentLines.add(leftBorder);

            // Third Riga
            switch (connectors[3]) {
                case ConnectorType.EMPTY, ConnectorType.DOUBLE -> {
                    leftBorder = " " + vBorder;
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.DOUBLE -> leftBorder = leftBorder + icon().getFirst() + vBorder + " ";
                        case ConnectorType.SINGLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder + icon().getFirst() + "├" + hBorder;
                    }
                }
                case ConnectorType.SINGLE, ConnectorType.UNIVERSAL -> {
                    leftBorder = hBorder + "┤";
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.DOUBLE -> leftBorder = leftBorder + icon().getFirst() + vBorder + " ";
                        case ConnectorType.SINGLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder + icon().getFirst() + "├" + hBorder;
                    }
                }
            }
            componentLines.add(leftBorder);

            // Fourth Riga
            switch (connectors[3]) {
                case ConnectorType.EMPTY, ConnectorType.SINGLE -> {
                    leftBorder = " " + vBorder;
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder + icon().get(1) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder + icon().get(1) + "├" + hBorder;
                    }
                }
                case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> {
                    leftBorder = hBorder + "┤";
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder + icon().get(1) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder + icon().get(1) + "├" + hBorder;
                    }
                }
            }
            componentLines.add(leftBorder);

            // Fifth Riga
            String bottomBorder = " " + angles[2];
            if (connectors[2] == ConnectorType.EMPTY)
                bottomBorder = bottomBorder + Constants.repeat(hBorder, 9) + angles[3] + " ";
            else if (connectors[2]==ConnectorType.SINGLE)
                bottomBorder = bottomBorder + Constants.repeat(hBorder, 4) + "┬" + Constants.repeat(hBorder, 4) + angles[3] + " ";
            else if (connectors[2]==ConnectorType.DOUBLE)
                bottomBorder = bottomBorder + hBorder + "┬" + Constants.repeat(hBorder, 5) + "┬" + hBorder +angles[3] + " ";
            else if (connectors[2]==ConnectorType.UNIVERSAL)
                bottomBorder = bottomBorder + hBorder + "┬" + Constants.repeat(hBorder, 2) + "┬" + Constants.repeat(hBorder, 2) + "┬" + hBorder + angles[3] + " ";
            componentLines.add(bottomBorder);

            return String.join("\n", componentLines);
        }
    }

    public List<String> icon() {
        return new ArrayList<>(List.of(
            Constants.repeat(" ", 9),
            Constants.repeat(" ", 9),
            Constants.repeat(" ", 9))
        );
    }

}
