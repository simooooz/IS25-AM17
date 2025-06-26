package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.ConnectorType;

import java.util.List;

public class CargoHoldsComponentDTO extends SpecialCargoHoldsComponentDTO {

    public CargoHoldsComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int number, int rotationsCounter, List<ColorType> goods) {
        super(id, connectors, x, y, inserted, shown, rotationsCounter, number, goods);
    }

    public CargoHoldsComponentDTO() {}

}