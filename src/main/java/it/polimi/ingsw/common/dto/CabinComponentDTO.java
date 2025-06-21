package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ConnectorType;

public class CabinComponentDTO extends ComponentDTO {

    public AlienType alien;
    public int humans;
    public boolean isStarting;

    public CabinComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, AlienType alien, int humans, boolean isStarting) {
        super(id, connectors, x, y, inserted, shown);
        this.alien = alien;
        this.humans = humans;
        this.isStarting = isStarting;
    }

    public CabinComponentDTO() {}

}