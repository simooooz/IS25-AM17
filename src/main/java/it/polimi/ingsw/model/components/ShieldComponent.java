package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
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
}