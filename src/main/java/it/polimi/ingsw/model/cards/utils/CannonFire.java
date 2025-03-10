package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.List;
import java.util.Optional;

public class CannonFire {

    private final boolean isBig;
    private final DirectionType directionFrom;

    public CannonFire(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public void hit(Ship ship, int coord) throws Exception {
        if (coord > 10 || coord < 4) return; // Miss

        List<Component> targets = directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targets.isEmpty()) return; // Miss
        Component target = targets.getFirst();

        if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) { // Ask user if he wants to use a battery
            Optional<BatteryComponent> chosenComponent = Optional.empty(); // View
            if (chosenComponent.isPresent()) {
                chosenComponent.get().useBattery(ship);
                return;
            }
        }

        target.destroyComponent(ship); // Destroy component
    }
}


