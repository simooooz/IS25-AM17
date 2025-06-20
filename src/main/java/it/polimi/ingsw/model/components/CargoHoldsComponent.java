package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.Ship;

public final class CargoHoldsComponent extends SpecialCargoHoldsComponent {

    public CargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors, number);
    }

    @Override
    public void loadGood(ColorType good, Ship ship) {
        if (good == ColorType.RED) throw new GoodNotValidException("Red good in normal hold");
        super.loadGood(good, ship);
    }

    @Override
    public void unloadGood(ColorType good, Ship ship) {
        if (good == ColorType.RED) throw new GoodNotValidException("Red good in normal hold");
        super.unloadGood(good, ship);
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == CargoHoldsComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == CargoHoldsComponent.class ||
                type == SpecialCargoHoldsComponent.class ||
                type.isAssignableFrom(this.getClass())) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast CargoHoldsComponent to " + type.getName());
    }

}
