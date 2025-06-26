package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.ConnectorType;

import java.util.List;

public class SpecialCargoHoldsComponentDTO extends ComponentDTO {

    public int number;
    public List<ColorType> goods;

    public SpecialCargoHoldsComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int number, int rotationsCounter, List<ColorType> goods) {
        super(id, connectors, x, y, inserted, shown, rotationsCounter);
        this.number = number;
        this.goods = goods;
    }

    public SpecialCargoHoldsComponentDTO() {}

}
