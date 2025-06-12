package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

public class CargoHoldsComponent extends SpecialCargoHoldsComponent {

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

    public String getColor() {
        return Chroma.WHITE_BACKGROUND;
    }

}
