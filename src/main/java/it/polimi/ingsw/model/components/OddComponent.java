package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.Ship;

public class OddComponent extends Component {

    private final AlienType type;

    public OddComponent(ConnectorType[] connectors, AlienType type) {
        super(connectors);
        this.type = type;
    }

    public AlienType getType() {
        return type;
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        // TODO deve cancellare gli alieni alle cabine vicine getLinkedNeighbors()
    }

}
