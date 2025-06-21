package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.BatteryComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.game.BatteriesUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

public final class BatteryComponent extends Component {

    private int batteries;
    private final boolean isTriple;

    public BatteryComponent(int id, ConnectorType[] connectors, boolean isTriple) {
        super(id, connectors);
        this.isTriple = isTriple;
        this.batteries = isTriple ? 3 : 2;
    }

    public int getBatteries() {
        return batteries;
    }

    public void useBattery(Ship ship) {
        if (batteries == 0) throw new BatteryComponentNotValidException("Not enough batteries");
        batteries--;
        ship.setBatteries(ship.getBatteries() - 1);
        EventContext.emit(new BatteriesUpdatedEvent(getId(), batteries));
    }

    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        super.insertComponent(player, row, col, rotations, weld);
        player.getShip().setBatteries(player.getShip().getBatteries() + batteries);
    }

    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        player.getShip().setBatteries(player.getShip().getBatteries() - batteries);
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == BatteryComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == BatteryComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast BatteryComponent to " + type.getName());
    }

    @Override
    public ComponentDTO toDTO() {
        return new BatteryComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), isTriple, batteries);
    }

}
