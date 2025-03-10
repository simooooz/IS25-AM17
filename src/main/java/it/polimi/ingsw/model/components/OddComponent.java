package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.Optional;

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
    public void affectDestroy(Ship ship) throws Exception {
        super.affectDestroy(ship);

        List<Component> linkedNeighbors = this.getLinkedNeighbors(ship);
        Optional<CabinComponent> linkedCabin = linkedNeighbors.stream()
            .filter(c -> c instanceof CabinComponent)
            .map(c -> (CabinComponent) c)
            .filter(c -> c.getAlien().isPresent() && c.getAlien().get() == type)
            .findFirst();

        if (linkedCabin.isPresent()) {
            List<Component> linkedLinkedNeighbors = linkedCabin.get().getLinkedNeighbors(ship);
            Optional<OddComponent> anotherOdd = linkedLinkedNeighbors.stream()
                .filter(c -> c instanceof OddComponent)
                .map(c -> (OddComponent) c)
                .filter(o -> o.getType() == type && !o.equals(this))
                .findFirst();

            if (anotherOdd.isEmpty()) { // Remove alien in linkedCabin
                linkedCabin.get().setAlien(null, ship);
            }
        }
    }

}
