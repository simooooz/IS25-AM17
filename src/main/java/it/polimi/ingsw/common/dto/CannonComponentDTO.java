package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class CannonComponentDTO extends ComponentDTO {

    public DirectionType direction;
    public boolean isDouble;

    public CannonComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int rotationsCounter, DirectionType direction, boolean isDouble) {
        super(id, connectors, x, y, inserted, shown, rotationsCounter);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public CannonComponentDTO() {}

}