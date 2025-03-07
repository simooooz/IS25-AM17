package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.properties.DirectionType;

public class CannonComponent extends Component {

    private final DirectionType direction;
    private final boolean isDouble;

    public CannonComponent(ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public DirectionType getDirection() {
        return direction;
    }

    public boolean getIsDouble() {
        return isDouble;
    }

}
