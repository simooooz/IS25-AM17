package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

public class ShieldComponent extends Component {

    private final DirectionType[] directionsProtected;

    public ShieldComponent(ConnectorType[] connectors, DirectionType[] directionsProtected) {
        super(connectors);
        this.directionsProtected = directionsProtected;
    }

    public DirectionType[] getDirectionsProtected() {
        return directionsProtected;
    }

    @Override
    public void insertComponent(Ship ship, int row, int col) {
        super.insertComponent(ship, row, col);
        for (DirectionType direction : directionsProtected)
            ship.getProtectedSides().add(direction);
    }

    @Override
    public void rotateComponent(Ship ship) {
        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[(this.directionsProtected[0].ordinal() + 1 % 4)];
        this.directionsProtected[1] = directions[(this.directionsProtected[1].ordinal() + 1 % 4)];
        super.rotateComponent(ship);
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        ship.getProtectedSides().remove(directionsProtected[0]);
        ship.getProtectedSides().remove(directionsProtected[1]);
    }

}