package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.CargoHoldsComponentDTO;
import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.GoodsUpdatedEvent;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;

public sealed class SpecialCargoHoldsComponent extends Component permits CargoHoldsComponent {

    private final int number;
    private final List<ColorType> goods;

    public SpecialCargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors);
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
        EventContext.emit(new GoodsUpdatedEvent(getId(), goods));
    }

    public void unloadGood(ColorType good, Ship ship) {
        if (goods.isEmpty() || !goods.contains(good)) throw new GoodNotValidException("Cargo hold is empty");
        goods.remove(good);
        ship.getGoods().put(good, ship.getGoods().get(good) - 1);
        EventContext.emit(new GoodsUpdatedEvent(getId(), goods));
    }

    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        for (ColorType good : goods) {
            player.getShip().getGoods().put(good, player.getShip().getGoods().get(good) - 1);
        }
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == SpecialCargoHoldsComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == SpecialCargoHoldsComponent.class || type.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast " + this.getClass().getName() + " to " + type.getName());
    }

    @Override
    public ComponentDTO toDTO() {
        return new CargoHoldsComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), number, goods);
    }

}