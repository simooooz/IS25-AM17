package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.common.model.Pair;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.Comparator;
import java.util.Optional;

public enum CriteriaType {
    CREW {
        @Override
        public PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst) {
            if (player.getShip().getCrew() < worst.getValue() || worst.getKey().isEmpty()) {
                worst.setKey(Optional.of(player.getUsername()));
                worst.setValue((double) player.getShip().getCrew());
            }
            return PlayerState.DONE;
        }
    },

    CANNON {
        @Override
        public PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst) {

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(CannonComponent::calcPower).sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                .filter(CannonComponent::getIsDouble)
                .mapToDouble(CannonComponent::calcPower)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(player.getShip().getBatteries())
                .mapToDouble(v -> v)
                .sum();

            if (freeCannonsPower >= worst.getValue() && worst.getKey().isPresent())
                return PlayerState.DONE;
            else if (doubleCannonsPower != 0)
                return PlayerState.WAIT_CANNONS;
            else { // User cannon activate double cannons, update worst
                worst.setKey(Optional.of(player.getUsername()));
                worst.setValue(freeCannonsPower);
                return PlayerState.DONE;
            }
        }
    },

    ENGINE {
        @Override
        public PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst) {

            double freeEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToDouble(EngineComponent::calcPower).sum();
            if (freeEnginesPower > 0 && player.getShip().getEngineAlien())
                freeEnginesPower += 2;

            double doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToDouble(EngineComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (freeEnginesPower >= worst.getValue() && worst.getKey().isPresent())
                return PlayerState.DONE;
            else if (doubleEnginesPower != 0)
                return PlayerState.WAIT_ENGINES;
            else { // User cannon activate double engines, update worst
                worst.setKey(Optional.of(player.getUsername()));
                worst.setValue(freeEnginesPower);
                return PlayerState.DONE;
            }
        }
    };

    public abstract PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst);
}
