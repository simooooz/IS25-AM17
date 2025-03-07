package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;

public class OddComponent extends Component {

    private final AlienType type;

    public OddComponent(ConnectorType[] connectors, AlienType type) {
        super(connectors);
        this.type = type;
    }

    public AlienType getType() {
        return type;
    }

}
