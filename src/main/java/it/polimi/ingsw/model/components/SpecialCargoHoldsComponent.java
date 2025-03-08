package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Good;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialCargoHoldsComponent extends Component {

    private final int number;
    private final List<ColorType> goods;

    public SpecialCargoHoldsComponent(ConnectorType[] connectors, int number) {
        super(connectors);
        this.number = number;
        this.goods = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public List<ColorType> getGoods() {
        return goods;
    }

}
