package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ConnectorType;

public class BatteryComponentDTO extends ComponentDTO{

    public boolean isTriple;
    public int batteries;

    public BatteryComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int rotationsCounter, boolean isTriple, int batteries) {
        super(id, connectors, x, y, inserted, shown, rotationsCounter);
        this.isTriple = isTriple;
        this.batteries = batteries;
    }

    public BatteryComponentDTO() {}

}
