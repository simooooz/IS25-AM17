package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;

public class EngineCommand implements Command<Integer> {

    private final Ship ship;
    private final List<BatteryComponent> batteries;
    private final List<EngineComponent> engines;

    public EngineCommand(Ship ship, List<BatteryComponent> batteries, List<EngineComponent> engines) {
        this.ship = ship;
        this.batteries = batteries;
        this.engines = engines;
    }

    @Override
    public Integer execute() {
        // TODO check correttezza capiamo dove farlo, è più generale
        // Sono nella nave giusta
        // Sono componenti diversi e corrispondono al tipo di componente giusto
        // Ci sono abbastanza batterie

        int singleEnginePower = ship.getComponentByType(EngineComponent.class).stream()
                .filter(engine -> !engine.getIsDouble())
                .toList().size();
        int dobuleEnginePower = engines.stream().mapToInt(EngineComponent::calcPower).sum();
        int userEnginePower = singleEnginePower + dobuleEnginePower + (ship.getEngineAlien() ? 2 : 0);

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return userEnginePower;
    }

}
