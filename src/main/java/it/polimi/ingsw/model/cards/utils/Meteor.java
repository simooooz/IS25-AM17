package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.Optional;

public class Meteor {
    private final boolean isBig;
    private final DirectionType directionFrom;

    public Meteor(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public void hit(Ship ship, int coord) throws Exception {
        if (coord > 10 || coord < 4) return; // Miss

        Optional<Component> targetOpt = directionFrom.firstComponentFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targetOpt.isEmpty()) return; // Miss
        Component target = targetOpt.get();

        if (!isBig && target.getConnectors()[directionFrom.ordinal()] == ConnectorType.EMPTY) // Ship is safe
            return;
        else if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) { // Ask user if he wants to use a battery
            Optional<BatteryComponent> chosenComponent = Optional.empty(); // View
            if (chosenComponent.isPresent()) { // Ship is safe
                chosenComponent.get().useBattery(ship);
                return;
            }
        }
        else if (isBig) {
            // TODO
            // Ask user if he wants to activate cannon
        }

        target.destroyComponent(ship); // Destroy component
    }

}
