package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class OddComponent extends Component {

    private final AlienType type;

    public OddComponent(int id, ConnectorType[] connectors, AlienType type) {
        super(id, connectors);
        this.type = type;
    }

    public AlienType getType() {
        return type;
    }

    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        Ship ship = player.getShip();

        if (type == AlienType.CANNON && !ship.getCannonAlien() || type == AlienType.ENGINE && !ship.getEngineAlien())
            return;

        Optional<CabinComponent> linkedCabin = this.getLinkedNeighbors(ship).stream()
                .filter(c -> c instanceof CabinComponent)
                .map(c -> (CabinComponent) c)
                .filter(c -> c.getAlien().isPresent() && c.getAlien().get() == type)
                .findFirst();

        if (linkedCabin.isPresent()) {
            Optional<OddComponent> anotherOdd = linkedCabin.get().getLinkedNeighbors(ship).stream()
                    .filter(c -> c instanceof OddComponent)
                    .map(c -> (OddComponent) c)
                    .filter(o -> o.getType() == type && !o.equals(this))
                    .findFirst();

            if (anotherOdd.isEmpty()) // Remove alien in linkedCabin
                linkedCabin.get().setAlien(null, ship);
        }
    }

}
