package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ConnectorType;

public class OddComponentDTO extends ComponentDTO {

    public AlienType type;

    public OddComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, AlienType type) {
        super(id, connectors, x, y, inserted, shown);
        this.type = type;
    }

    public OddComponentDTO() {}

}