package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.util.List;
import java.util.Optional;

public class CannonFire {

    @JsonProperty private final boolean isBig;
    @JsonProperty private final DirectionType directionFrom;

    public CannonFire(boolean isBig, DirectionType directionFrom) {
        this.isBig = isBig;
        this.directionFrom = directionFrom;
    }

    public PlayerState hit(PlayerData player, int coord) {
        if (coord > 10 || coord < 4) return PlayerState.DONE; // Miss
        Ship ship = player.getShip();

        List<Component> targets = directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord); // Find hit component
        if (targets.isEmpty()) return PlayerState.DONE; // Miss
        Component target = targets.getFirst();

        if (!isBig && ship.getProtectedSides().contains(directionFrom) && ship.getBatteries() > 0) // Ask user if he wants to use a battery
            return PlayerState.WAIT_SHIELD;

        return target.destroyComponent(player); // Destroy component
    }

    public Optional<Component> getTarget(Ship ship, int coord) {
        return directionFrom.getComponentsFromThisDirection(ship.getDashboard(), coord).stream().findFirst();
    }

}


