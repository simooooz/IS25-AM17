package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;

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

    public void loadGood(ColorType good, Ship ship) {
        if (number == goods.size()) throw new GoodNotValidException("Cargo hold is full");
        goods.add(good);
        ship.getGoods().put(good, ship.getGoods().get(good) + 1);
    }

    public void unloadGood(ColorType good, Ship ship) {
        if (goods.isEmpty() || !goods.contains(good)) throw new GoodNotValidException("Cargo hold is empty");
        goods.remove(good);
        ship.getGoods().put(good, ship.getGoods().get(good) - 1);
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        for (ColorType good : goods) {
            ship.getGoods().put(good, ship.getGoods().get(good) - 1);
        }
    }

}