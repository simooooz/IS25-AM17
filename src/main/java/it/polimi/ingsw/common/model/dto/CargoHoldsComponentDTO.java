package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.ConnectorType;

import java.util.List;

public class CargoHoldsComponentDTO extends ComponentDTO {

    public int number;
    public List<ColorType> goods;

    public CargoHoldsComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int number, List<ColorType> goods) {
        super(id, connectors, x, y, inserted, shown);
        this.number = number;
        this.goods = goods;
    }

    public CargoHoldsComponentDTO() {}

}