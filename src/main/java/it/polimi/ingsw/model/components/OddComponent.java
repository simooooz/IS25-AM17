package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.dto.OddComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public final class OddComponent extends Component {

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
                .filter(c -> c.matchesType(CabinComponent.class))
                .map(c -> c.castTo(CabinComponent.class))
                .filter(c -> c.getAlien().isPresent() && c.getAlien().get() == type)
                .findFirst();

        if (linkedCabin.isPresent()) {
            Optional<OddComponent> anotherOdd = linkedCabin.get().getLinkedNeighbors(ship).stream()
                    .filter(c -> c.matchesType(OddComponent.class))
                    .map(c -> c.castTo(OddComponent.class))
                    .filter(o -> o.getType() == type && !o.equals(this))
                    .findFirst();

            if (anotherOdd.isEmpty()) // Remove alien in linkedCabin
                linkedCabin.get().setAlien(null, ship);
        }
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == OddComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == OddComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast OddComponent to " + type.getName());
    }

    @Override
    public ComponentDTO toDTO() {
        return new OddComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), type);
    }

}
