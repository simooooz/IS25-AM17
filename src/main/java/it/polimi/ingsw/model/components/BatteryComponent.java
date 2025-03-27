package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.player.Ship;

public class BatteryComponent extends Component {

    private final boolean isTriple;
    private int batteries;

    public BatteryComponent(ConnectorType[] connectors, boolean isTriple) {
        super(connectors);
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
    }

    @Override
    public void insertComponent(Ship ship, int row, int col) {
        super.insertComponent(ship, row, col);
        if (isTriple) { ship.setBatteries(ship.getBatteries() + 3); }
        else { ship.setBatteries(ship.getBatteries() + 2); }
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        ship.setBatteries(ship.getBatteries() - batteries);
    }

}
