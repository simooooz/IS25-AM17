package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class EngineComponentDTO extends ComponentDTO {

    public DirectionType direction;
    public boolean isDouble;

    public EngineComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, DirectionType direction, boolean isDouble) {
        super(id, connectors, x, y, inserted, shown);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public EngineComponentDTO() {}

}