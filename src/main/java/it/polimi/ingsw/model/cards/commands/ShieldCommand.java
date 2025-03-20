package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class ShieldCommand implements Command<Boolean> {

    private final Ship ship;
    private final Optional<BatteryComponent> battery;

    public ShieldCommand(Ship ship, BatteryComponent battery) {
        this.ship = ship;
        this.battery = Optional.ofNullable(battery);
    }

    @Override
    public Boolean execute() {
        if (battery.isPresent()) {
            battery.get().useBattery(ship);
            return true;
        }
        return false;
    }

}
