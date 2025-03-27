package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;
import java.util.Optional;

public enum CriteriaType {
    CREW {
        @Override
        public PlayerState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) {
            if (player.getShip().getCrew() < worst.getValue() || worst.getKey().getValue().isEmpty()) {
                worst.getKey().setValue(Optional.of(player));
                worst.setValue((double) player.getShip().getCrew());
            }
            return PlayerState.DONE;
        }
    },

    CANNON {
        @Override
        public PlayerState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) {

            double freeCannonsPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(CannonComponent::calcPower).sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                .filter(CannonComponent::getIsDouble)
                .mapToDouble(CannonComponent::calcPower)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(player.getShip().getBatteries())
                .mapToDouble(v -> v)
                .sum();

            if (freeCannonsPower >= worst.getValue() && worst.getKey().getValue().isPresent())
                return PlayerState.DONE;
            else if (doubleCannonsPower != 0)
                return PlayerState.WAIT_CANNONS;
            else { // User cannon activate double cannons, update worst
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(freeCannonsPower);
                return PlayerState.DONE;
            }
        }
    },

    ENGINE {
        @Override
        public PlayerState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) {

            double freeEnginesPower = (player.getShip().getEngineAlien() ? 2 : 0) + player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToDouble(EngineComponent::calcPower).sum();
            double doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToDouble(EngineComponent::calcPower)
                    .limit(player.getShip().getBatteries())
                    .sum();

            if (freeEnginesPower >= worst.getValue() && worst.getKey().getValue().isPresent())
                return PlayerState.DONE;
            else if (doubleEnginesPower != 0)
                return PlayerState.WAIT_ENGINES;
            else { // User cannon activate double engines, update worst
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(freeEnginesPower);
                return PlayerState.DONE;
            }
        }
    };

    public abstract PlayerState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst);
}
