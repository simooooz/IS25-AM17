package it.polimi.ingsw.model.components;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.ArrayList;
import java.util.List;

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
    public void insertComponent(Ship ship, int row, int col, int rotations, boolean learnerMode) {
        super.insertComponent(ship, row, col, rotations, learnerMode);
        for (DirectionType direction : directionsProtected)
            ship.getProtectedSides().add(direction);
    }

    @Override
    public void rotateComponent(Ship ship) {
        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + 1) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + 1) % 4)];
        super.rotateComponent(ship);
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        ship.getProtectedSides().remove(directionsProtected[0]);
        ship.getProtectedSides().remove(directionsProtected[1]);
    }

    @Override
    public List<String> icon() {
        List<String> icon = new ArrayList<>();
        if (directionsProtected[0] == DirectionType.NORTH || directionsProtected[1] == DirectionType.NORTH) {
            if (directionsProtected[0] == DirectionType.EAST || directionsProtected[1] == DirectionType.EAST) {
                icon.add(" ⬆️"+ "\u200A" + "🛡️" + "\u200A" + "➡️ ");
            }
            else if (directionsProtected[0] == DirectionType.WEST || directionsProtected[1] == DirectionType.WEST) {
                icon.add(" ⬅️"+ "\u200A" + "🛡️" + "\u200A" + "⬆️️️ ");
            }
        }
        else if (directionsProtected[0] == DirectionType.SOUTH || directionsProtected[1] == DirectionType.SOUTH){
            if (directionsProtected[0] == DirectionType.EAST || directionsProtected[1] == DirectionType.EAST) {
                icon.add(" ⬇️"+ "\u200A" + "🛡️" + "\u200A" + "➡️ ");
            }
            else if (directionsProtected[0] == DirectionType.WEST || directionsProtected[1] == DirectionType.WEST) {
                icon.add(" ⬅️" + "\u200A" + "🛡️" + "\u200A" + "⬇️️️ ");
            }
        }
        icon.add(Constants.repeat(" ", 9));

        return icon;
    }

}