package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.dto.*;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static it.polimi.ingsw.Constants.inTheMiddle;

/**
 * Abstract base class for all client-side components.
 * This class and its subclasses are simple data containers without business logic.
 * Jackson annotations are used to handle polymorphism during deserialization.
 */
public sealed class ClientComponent permits
    ClientBatteryComponent, ClientCabinComponent, ClientCannonComponent,
    ClientCargoHoldsComponent, ClientEngineComponent, ClientOddComponent, ClientShieldComponent
{

    private final int id;
    private ConnectorType[] connectors;
    private int x;
    private int y;
    private boolean inserted;
    private boolean shown;

    private final int rotationsCounter;

    public ClientComponent(int id, ConnectorType[] connectors) {
        this.id = id;
        this.connectors = connectors;
        this.inserted = false;
        this.shown = false;
        this.rotationsCounter = 0;
    }

    public ClientComponent(ComponentDTO dto) {
        this.id = dto.id;
        this.connectors = dto.connectors;
        this.inserted = dto.inserted;
        this.shown = dto.shown;
        this.rotationsCounter = dto.rotationCounter;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getRotationsCounter() {
        return rotationsCounter;
    }

    public ConnectorType[] getConnectors() {
        return connectors;
    }

    public void insertComponent(ClientPlayer player, int row, int col, int rotations, boolean weld) {
        ClientShip ship = player.getShip();

        if (ship.getComponentInHand().isPresent() && ship.getComponentInHand().get().equals(this)) // Component is in hand
            ship.setComponentInHand(null);
        else if (ship.getReserves().contains(this)) { // Component is in reserves, weld it
            ship.getReserves().remove(this);
            weld = true;
        }

        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        rotateComponent(player, rotations);

        // Eventually weld component
        this.inserted = weld;
    }

    public void rotateComponent() {
        ConnectorType[] newConnectors = new ConnectorType[4];
        newConnectors[0] = connectors[3];
        newConnectors[1] = connectors[0];
        newConnectors[2] = connectors[1];
        newConnectors[3] = connectors[2];
        connectors = newConnectors;
    }

    public void rotateComponent(ClientPlayer player, int rotations) {
        if (rotations % 4 == 0) return;
        ClientShip ship = player.getShip();
        if ((ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this)) && (ship.getComponentInHand().isEmpty() || !ship.getComponentInHand().get().equals(this)) && !ship.getReserves().contains(this))
            throw new ComponentNotValidException("Component isn't in hand or in dashboard or in reserves");

        if (inserted)
            throw new ComponentNotValidException("Component is already welded");

        for (int i=0; i<(rotations % 4); i++)
            rotateComponent();
    }

    public void moveComponent(ClientPlayer player, int row, int col, int rotations) {
        ClientShip ship = player.getShip();
        ship.getDashboard()[y][x] = Optional.empty();
        this.x = col;
        this.y = row;
        ship.getDashboard()[row][col] = Optional.of(this);

        rotateComponent(player, rotations);
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isInserted() {
        return inserted;
    }

    protected void setInserted() {
        this.inserted = true;
    }

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};
        List<String> componentLines = new ArrayList<>();
        String topBorder;
        if (!shown) {
            topBorder = " " + angles[0] + Constants.repeat(hBorder, 11) + angles[1] + " ";
            componentLines.add(topBorder);

            String leftBorder;
            leftBorder = " " + vBorder + inTheMiddle(String.valueOf(this.id), 11) + vBorder + " ";
            componentLines.add(leftBorder);

            leftBorder = " " + vBorder + Constants.repeat(" ", 11) + vBorder + " ";
            componentLines.add(leftBorder);

            leftBorder = " " + vBorder + Constants.repeat(" ", 11) + vBorder + " ";
            componentLines.add(leftBorder);

            String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 11) + angles[3] + " ";
            componentLines.add(bottomBorder);

        }
        else {
            // First row
            topBorder = " " + angles[0];

            if (connectors[0] == ConnectorType.EMPTY)
                topBorder = topBorder + Constants.repeat(hBorder, 11) + angles[1] + " ";
            else if (connectors[0]==ConnectorType.SINGLE)
                topBorder = topBorder + Constants.repeat(hBorder, 5) + "┴" + Constants.repeat(hBorder, 5) + angles[1] + " ";
            else if (connectors[0]==ConnectorType.DOUBLE)
                topBorder = topBorder + hBorder + "┴" + Constants.repeat(hBorder, 7) + "┴" + hBorder + angles[1] + " ";
            else if (connectors[0]==ConnectorType.UNIVERSAL)
                topBorder = topBorder + hBorder + "┴" + Constants.repeat(hBorder, 3) + "┴" + Constants.repeat(hBorder, 3) + "┴" + hBorder + angles[1] + " ";
            componentLines.add(topBorder);

            // Second Riga
            String leftBorder = "";
            switch (connectors[3]) {
                case ConnectorType.EMPTY, ConnectorType.SINGLE -> {
                    leftBorder = " " + vBorder;
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder +
                                inTheMiddle(String.valueOf(this.id), 11) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder +
                                inTheMiddle(String.valueOf(this.id), 11) + "├" + hBorder;
                    }
                }
                case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> {
                    leftBorder = hBorder + "┤";
                    switch (connectors[1]) {
                        case ConnectorType.EMPTY, ConnectorType.SINGLE -> leftBorder = leftBorder +
                                inTheMiddle(String.valueOf(this.id), 11) + vBorder + " ";
                        case ConnectorType.DOUBLE, ConnectorType.UNIVERSAL -> leftBorder = leftBorder +
                                inTheMiddle(String.valueOf(this.id), 11) + "├" + hBorder;
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
                bottomBorder = bottomBorder + Constants.repeat(hBorder, 11) + angles[3] + " ";
            else if (connectors[2]==ConnectorType.SINGLE)
                bottomBorder = bottomBorder + Constants.repeat(hBorder, 5) + "┬" + Constants.repeat(hBorder, 5) + angles[3] + " ";
            else if (connectors[2]==ConnectorType.DOUBLE)
                bottomBorder = bottomBorder + hBorder + "┬" + Constants.repeat(hBorder, 7) + "┬" + hBorder +angles[3] + " ";
            else if (connectors[2]==ConnectorType.UNIVERSAL)
                bottomBorder = bottomBorder + hBorder + "┬" + Constants.repeat(hBorder, 3) + "┬" + Constants.repeat(hBorder, 3) + "┬" + hBorder + angles[3] + " ";
            componentLines.add(bottomBorder);

        }
        return String.join("\n", componentLines);
    }

    public List<String> icon() {
        return new ArrayList<>(List.of(
                Constants.repeat(" ", 11),
                Constants.repeat(" ", 11),
                Constants.repeat(" ", 11))
        );
    }

}