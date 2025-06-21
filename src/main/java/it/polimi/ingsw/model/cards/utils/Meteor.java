package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;


import java.util.List;
import java.util.Optional;

public class Meteor {

    @JsonProperty private final boolean isBig;
    @JsonProperty private final DirectionType directionFrom;

    public Meteor(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public PlayerState hit(PlayerData player, int coord) {
        if (coord > 10 || coord < 4) return PlayerState.DONE; // Miss
        Ship ship = player.getShip();

        List<Component> targets = getTargets(ship, coord); // Find hit component
        if (targets.isEmpty()) return PlayerState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && target.getConnectors()[directionFrom.ordinal()] == ConnectorType.EMPTY) // Ship is safe
            return PlayerState.DONE;
        else if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0)// Ask user if he wants to use a battery
            return PlayerState.WAIT_SHIELD;
        else if (isBig) {

            if (directionFrom != DirectionType.NORTH) {
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord-1));
                targets.addAll(directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord+1));
            }

            List<CannonComponent> cannonsOverLine = targets.stream()
                .filter(c -> c.matchesType(CannonComponent.class))
                .map(c -> c.castTo(CannonComponent.class))
                .filter(c -> c.getDirection() == directionFrom)
                .toList();

            Optional<CannonComponent> singleCannon = cannonsOverLine.stream().filter(c -> !c.getIsDouble()).findFirst();
            if (singleCannon.isPresent()) return PlayerState.DONE;

            if (!cannonsOverLine.isEmpty()) // There is a double cannon that could destroy meteor
                return PlayerState.WAIT_CANNONS;

        }
        return target.destroyComponent(player); // Destroy component
    }

    public List<Component> getTargets(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord);
    }

    public DirectionType getDirectionFrom() {
        return directionFrom;
    }

}
