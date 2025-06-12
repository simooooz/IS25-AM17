package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.cards.PlayerState;
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

    public PlayerState hit(Ship ship, int coord) {
        if (coord > 10 || coord < 4) return PlayerState.DONE; // Miss

        List<Component> targets = directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targets.isEmpty()) return PlayerState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) // Ask user if he wants to use a battery
            return PlayerState.WAIT_SHIELD;

        return target.destroyComponent(ship); // Destroy component
    }

    public Optional<Component> getTarget(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord).stream().findFirst();
    }

    @Override
    public String toString() {
        String fire = "\uD83D\uDD25";
        String arrow = "";
        switch (directionFrom) {
            case NORTH: arrow = "↓"; break;
            case SOUTH: arrow = "↑"; break;
            case EAST: arrow = "←"; break;
            case WEST: arrow = "→"; break;

        }
        return  (isBig ? Constants.inTheMiddle(fire + fire + " ", 7)
                : Constants.inTheMiddle(fire + " ", 7)) + arrow;
    }
}


