package it.polimi.ingsw.model.components;

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

    public void affectDestroy(PlayerData player) {
        Ship ship = player.getShip();
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Component isn't in dashboard");

        ship.getDiscards().add(this);
        ship.getDashboard()[y][x] = Optional.empty();
        EventContext.emit(new ComponentDestroyedEvent(player.getUsername(), id));
    }

    public void showComponent() {
        this.shown = true;
    }

    /**
     * Handles the selection of this(component) by a ship from the
     * board's list with the components available
     *
     * @param board Board ref
     * @param player Player ref
     * @throws ComponentNotValidException if the component is not pickable
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
     * Releases to the board this(component) from the hand, or ship if not welded yet
     *
     * @param board
     * @param player
     */
    public void releaseComponent(Board board, PlayerData player) {
        Ship ship = player.getShip();
        if (board.getCommonComponents().contains(this) || !shown)
            throw new ComponentNotValidException("This component is already released");
        if (inserted)
            throw new ComponentNotValidException("Component is welded");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component to release is in hand
            ship.setHandComponent(null);
        else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) // Component to release is in dashboard
            ship.getDashboard()[y][x] = Optional.empty();

        board.getCommonComponents().add(this);
        EventContext.emit(new ComponentReleasedEvent(player.getUsername(), id));
    }

    public void reserveComponent(PlayerData player) {
        Ship ship = player.getShip();
        if (ship.getReserves().size() >= 2)
            throw new ComponentNotValidException("Reserves are full");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) { // Component to reserve is in hand
            ship.setHandComponent(null);
        }
        else if (ship.getDashboard(y, x).isPresent() && ship.getDashboard(y, x).get().equals(this)) { // Component to reserve is in dashboard
            if (inserted)
                throw new ComponentNotValidException("Component is already welded");
            ship.getDashboard()[y][x] = Optional.empty();
        }
        else
            throw new ComponentNotValidException("Component to reserve isn't in hand or in dashboard");

        ship.getReserves().add(this);
        EventContext.emit(new ComponentReservedEvent(player.getUsername(), id));
    }

    // TODO remove weld param
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        Ship ship = player.getShip();

        if (!ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("The position where to insert it is not valid"); // Check if new position is valid
        else if (!shown)
            throw new ComponentNotValidException("Component is hidden");

        if (ship.getHandComponent().isPresent() && ship.getHandComponent().get().equals(this)) // Component is in hand
            ship.setHandComponent(null);
        else if (ship.getReserves().contains(this)) { // Component is in reserves, weld it
            ship.getReserves().remove(this);
            weld = true;
        }
        else
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

    public void weldComponent() {
        inserted = true;
    }

    public void moveComponent(PlayerData player, int row, int col, int rotations) {
        Ship ship = player.getShip();
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Component isn't in dashboard");
        else if (inserted)
            throw new ComponentNotValidException("Component already welded");
        else if (!ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("New position isn't valid or is already occupied"); // Check if new position is valid

        ship.getDashboard()[y][x] = Optional.empty();
        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        rotateComponent(player, rotations);

        EventContext.emit(new ComponentMovedEvent(player.getUsername(), id, row, col));
    }

    public void rotateComponent(PlayerData player, int rotations) {
        Ship ship = player.getShip();
        if ((ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this)) && (ship.getHandComponent().isEmpty() || !ship.getHandComponent().get().equals(this)))
            throw new ComponentNotValidException("Component isn't in hand or in dashboard");

        if (inserted)
            throw new ComponentNotValidException("Component is already welded");

        for (int i=0; i<(rotations % 4); i++) {
            ConnectorType[] newConnectors = new ConnectorType[4];
            newConnectors[0] = connectors[3];
            newConnectors[1] = connectors[0];
            newConnectors[2] = connectors[1];
            newConnectors[3] = connectors[2];
            connectors = newConnectors;
        }

        EventContext.emit(new ComponentRotatedEvent(id, rotations % 4));
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
    public PlayerState destroyComponent(PlayerData player) {
        affectDestroy(player);
        List<List<Component>> groups = player.getShip().calcShipParts();

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

    public int getId() {
        return id;
    }

}
