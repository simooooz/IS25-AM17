package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum CriteriaType {
    CREW {
        @Override
        public double countCriteria(PlayerData player) {
            return player.getShip().getCrew();
        }
    },

    CANNON {
        @Override
        public double countCriteria(PlayerData player) throws Exception {
            double singleCannonsPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 1 : 0.5).sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            double userFirePower = singleCannonsPower;
            if (doubleCannonsPower > 0 && player.getShip().getBatteries() > 0) { // Player has double cannons
                List<CannonComponent> cannonsToActivate = new ArrayList<>(); // View => user select which cannons wants to activate
                if (player.getShip().getBatteries() < cannonsToActivate.size()) throw new Exception(); // Not enough batteries

                for (CannonComponent doubleCannon : cannonsToActivate) // Calculate firepower
                    userFirePower += doubleCannon.getDirection() == DirectionType.NORTH ? 2 : 1;

                for (int i = 0; i < cannonsToActivate.size(); i++) { // Remove batteries
                    Optional<BatteryComponent> chosenComponentOpt = Optional.empty(); // View => Ask the user
                    BatteryComponent chosenComponent = chosenComponentOpt.orElseThrow();
                    chosenComponent.useBattery(player.getShip());
                }
            }
            return userFirePower;
        }
    },

    ENGINE {
        @Override
        public double countCriteria(PlayerData player) throws Exception {
            double singleEnginePower = (player.getShip().getEngineAlien() ? 2 : 0) + player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .toList().size();
            double doubleEnginePower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .toList().size();

            double userEnginePower = singleEnginePower;
            if (doubleEnginePower > 0 && player.getShip().getBatteries() > 0) { // Player has double cannons
                List<EngineComponent> enginesToActivate = new ArrayList<>(); // View => user select which cannons wants to activate
                if (player.getShip().getBatteries() < enginesToActivate.size()) throw new Exception(); // Not enough batteries

                userEnginePower += enginesToActivate.size(); // Calculate engine power

                for (int i = 0; i < enginesToActivate.size(); i++) { // Remove batteries
                    Optional<BatteryComponent> chosenComponentOpt = Optional.empty(); // View => Ask the user
                    BatteryComponent chosenComponent = chosenComponentOpt.orElseThrow();
                    chosenComponent.useBattery(player.getShip());
                }
            }
            return userEnginePower;
        }
    };

    public abstract double countCriteria(PlayerData player) throws Exception;
}
