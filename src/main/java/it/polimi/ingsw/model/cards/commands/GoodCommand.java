package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoodCommand implements Command<Void> {
    private final Ship ship;
    private final Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds;
    private final Map<ColorType, Integer> rewards;

    public GoodCommand(Ship ship, Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds, Map<ColorType, Integer> rewards) {
        this.ship = ship;
        this.cargoHolds = cargoHolds;
        this.rewards = rewards;
    }

    @Override
    public Void execute() {
        Map<ColorType, Integer> deltaGood = new HashMap<>();
        List<SpecialCargoHoldsComponent> playerHolds = ship.getComponentByType(SpecialCargoHoldsComponent.class);

        // increases goods value for each good which is present after the call
        for (SpecialCargoHoldsComponent c : cargoHolds.keySet()) {
            for (int i = 0; i < cargoHolds.get(c).size(); i++) {
                deltaGood.put(cargoHolds.get(c).get(i), deltaGood.get(cargoHolds.get(c).get(i)) + 1);
            }
        }
        // decreases goods value for each good which is present before the call
        for (SpecialCargoHoldsComponent c : playerHolds) {
            for (ColorType good : c.getGoods()) {
                deltaGood.put(good, deltaGood.get(good) - 1);
            }
        }

        // check goods number
        for(ColorType good : ColorType.values()) {
            if(deltaGood.get(good) > rewards.get(good)) {
                throw new Exception();
            }
        }

        for (SpecialCargoHoldsComponent component : cargoHolds.keySet()) {
            for (ColorType good : component.getGoods()) {
                component.unloadGood(good, ship);
            }
            for (ColorType good : cargoHolds.get(component)) {
                component.loadGood(good, ship);
            }
        }

    }
}
