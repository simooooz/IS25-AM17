package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.CardState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.List;
import java.util.Optional;

public class Meteor {
    private final boolean isBig;
    private final DirectionType directionFrom;

    public Meteor(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public CardState hit(Ship ship, int coord) throws Exception {
        if (coord > 10 || coord < 4) return CardState.DONE; // Miss

        List<Component> targets = directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targets.isEmpty()) return CardState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && target.getConnectors()[directionFrom.ordinal()] == ConnectorType.EMPTY) // Ship is safe
            return CardState.DONE;
        else if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) // Ask user if he wants to use a battery
            return CardState.WAIT_SHIELD;
        else if (isBig) {

            if (directionFrom != DirectionType.NORTH) {
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord-1));
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord+1));
            }

            List<CannonComponent> cannonsOverLine = targets.stream()
                .filter(c -> c instanceof CannonComponent)
                .map(c -> (CannonComponent) c)
                .filter(c -> c.getDirection() == directionFrom)
                .toList();

            Optional<CannonComponent> singleCannon = cannonsOverLine.stream().filter(c -> !c.getIsDouble()).findFirst();
            if (singleCannon.isPresent()) return CardState.DONE;

            if (!cannonsOverLine.isEmpty()) { // There is a double cannon that could destroy meteor
                return CardState.WAIT_BOOLEAN;
            }

        }

        target.destroyComponent(ship); // Destroy component
        return CardState.DONE;
    }

    public Optional<Component> getTarget(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord).stream().findFirst();
    }

}
