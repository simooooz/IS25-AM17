package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;

public class CannonCommand implements Command<Double> {

    private final Ship ship;
    private final List<BatteryComponent> batteries;
    private final List<CannonComponent> cannons;

    public CannonCommand(Ship ship, List<BatteryComponent> batteries, List<CannonComponent> cannons) {
        this.ship = ship;
        this.batteries = batteries;
        this.cannons = cannons;
    }

    @Override
    public Double execute() {
        // TODO check correttezza capiamo dove farlo, è più generale
        // Sono nella nave giusta
        // Sono componenti diversi e corrispondono al tipo di componente giusto
        // Ci sono abbastanza batterie

        double singleCannonPower = ship.getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(CannonComponent::calcPower)
                .sum();
        double doubleCannonPower = cannons.stream().mapToDouble(CannonComponent::calcPower).sum();
        double userCannonPower =  + singleCannonPower + doubleCannonPower + (ship.getCannonAlien() ? 2 : 0);

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return userCannonPower;
    }

}
