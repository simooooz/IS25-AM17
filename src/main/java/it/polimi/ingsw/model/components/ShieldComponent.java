package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.common.model.enums.DirectionType;


public class ShieldComponent extends Component {

    private final DirectionType[] directionsProtected;

    public ShieldComponent(int id, ConnectorType[] connectors, DirectionType[] directionsProtected) {
        super(id, connectors);
        this.directionsProtected = directionsProtected;
    }

    public DirectionType[] getDirectionsProtected() {
        return directionsProtected;
    }

    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        super.insertComponent(player, row, col, rotations, weld);
        for (DirectionType direction : directionsProtected)
            player.getShip().getProtectedSides().add(direction);
    }

    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + rotations) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + rotations) % 4)];
    }

    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        player.getShip().getProtectedSides().remove(directionsProtected[0]);
        player.getShip().getProtectedSides().remove(directionsProtected[1]);
    }

}