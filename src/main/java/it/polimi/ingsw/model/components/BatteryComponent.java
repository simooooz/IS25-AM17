package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.game.BatteriesUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

public class BatteryComponent extends Component {

    private final boolean isTriple;
    private int batteries;

    public BatteryComponent(int id, ConnectorType[] connectors, boolean isTriple) {
        super(id, connectors);
        this.isTriple = isTriple;
        this.batteries = isTriple ? 3 : 2;
    }

    public boolean getIsTriple() {
        return isTriple;
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

}
