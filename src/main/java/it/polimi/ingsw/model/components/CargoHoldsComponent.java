package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;

public class CargoHoldsComponent extends SpecialCargoHoldsComponent {

    public CargoHoldsComponent(ConnectorType[] connectors, int number) {
        super(connectors, number);
    }

    @Override
    public void loadGood(ColorType good, Ship ship) throws Exception {
        if (good == ColorType.RED) throw new Exception();
        super.loadGood(good, ship);
    }

    @Override
    public void unloadGood(ColorType good, Ship ship) throws Exception {
        if (good == ColorType.RED) throw new Exception();
        super.loadGood(good, ship);
    }

}