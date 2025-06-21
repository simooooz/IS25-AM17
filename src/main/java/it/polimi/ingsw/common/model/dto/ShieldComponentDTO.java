package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class ShieldComponentDTO extends ComponentDTO {

    public DirectionType[] directionsProtected;

    public ShieldComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, DirectionType[] directionsProtected) {
        super(id, connectors, x, y, inserted, shown);
        this.directionsProtected = directionsProtected;
    }

    public ShieldComponentDTO() {}

}