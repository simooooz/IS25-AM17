package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
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
    public void rotateComponent(boolean clockwise) {
        DirectionType[] directions = DirectionType.values();
        if (clockwise) {
            this.directionsProtected[0] = directions[(this.directionsProtected[0].ordinal() + 1 % 4)];
            this.directionsProtected[1] = directions[(this.directionsProtected[1].ordinal() + 1 % 4)];
        }
        else {
            this.directionsProtected[0] = directions[(this.directionsProtected[0].ordinal() + 3 % 4)];
            this.directionsProtected[1] = directions[(this.directionsProtected[1].ordinal() + 3 % 4)];
        }
        super.rotateComponent(clockwise);
    }

}