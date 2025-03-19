package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.player.Ship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrewCommand implements Command<Void> {

    private final Ship ship;
    private final List<CabinComponent> cabinComponents;

    public CrewCommand(Ship ship, List<CabinComponent> cabinComponents) {
        this.ship = ship;
        this.cabinComponents = cabinComponents;
    }

    @Override
    public Void execute() {
        Map<CabinComponent, Integer> humanCabinCount = new HashMap<>();
        Map<CabinComponent, Integer> alienCabinCount = new HashMap<>();

        cabinComponents.forEach(cabinComponent -> {
            try {
                if (cabinComponent.getAlien().isPresent()) {
                    int count = alienCabinCount.getOrDefault(cabinComponent, 0);
                    if (count >= 1) {
                        throw new RuntimeException("Alien cabin can be in the list only one time");
                    }
                    alienCabinCount.put(cabinComponent, count + 1);

                    cabinComponent.setAlien(null, ship);
                } else {
                    int count = humanCabinCount.getOrDefault(cabinComponent, 0);
                    if (count >= 2) {
                        throw new RuntimeException("Human cabin can be in the list only two times");
                    }
                    humanCabinCount.put(cabinComponent, count + 1);

                    cabinComponent.setHumans(cabinComponent.getHumans()-1, ship);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }

}
