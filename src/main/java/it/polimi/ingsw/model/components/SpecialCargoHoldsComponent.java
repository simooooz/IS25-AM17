package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.Good;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialCargoHoldsComponent extends Component {

    private final int number;
    private final List<Optional<Good>> goods;

    public SpecialCargoHoldsComponent(ConnectorType[] connectors, int number) {
        super(connectors);
        this.number = number;
        this.goods = new ArrayList<Optional<Good>>();
    }

    public int getNumber() {
        return number;
    }

    public List<Optional<Good>> getGoods() {
        return goods;
    }

}
