package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;

public class BatteryComponent extends Component {

    private final boolean isTriple;
    private int batteries;

    public BatteryComponent(ConnectorType[] connectors, boolean isTriple, int batteries) {
        super(connectors);
        this.isTriple = isTriple;
        this.batteries = batteries;
    }

    public boolean getIsTriple() {
        return isTriple;
    }

    public int getBatteries() {
        return batteries;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

}
