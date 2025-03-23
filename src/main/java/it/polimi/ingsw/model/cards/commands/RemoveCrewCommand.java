package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveCrewCommand implements Command<Void> {

    private final Ship ship;
    private final List<CabinComponent> cabinComponents;

    public RemoveCrewCommand(Ship ship, List<CabinComponent> cabinComponents) {
        this.ship = ship;
        this.cabinComponents = cabinComponents;
    }

    @Override
    public Void execute() {
        for (CabinComponent cabin : cabinComponents) {
            if (cabin.getAlien().isPresent())
                cabin.setAlien(null, ship);
            else
                cabin.setHumans(cabin.getHumans() - 1, ship);

        }
        return null;
    }

}
