package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.CardState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public enum CriteriaType {
    CREW {
        @Override
        public CardState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) {
            if (player.getShip().getCrew() < worst.getValue() || worst.getKey().getValue().isEmpty()) {
                worst.getKey().setValue(Optional.of(player));
                worst.setValue((double) player.getShip().getCrew());
            }
            return CardState.DONE;
        }
    },

    CANNON {
        @Override
        public CardState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) throws Exception {

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
                return CardState.DONE;
            else if (doubleCannonsPower != 0)
                return CardState.WAIT_CANNON;
            else { // User cannon activate double cannons, update worst
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(freeCannonsPower);
                return CardState.DONE;
            }
        }
    },

    ENGINE {
        @Override
        public CardState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) throws Exception {

            double freeEnginesPower = (player.getShip().getEngineAlien() ? 2 : 0) + player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToDouble(EngineComponent::calcPower).sum();
            double doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToDouble(EngineComponent::calcPower)
                    .limit(player.getShip().getBatteries())
                    .sum();

            if (freeEnginesPower >= worst.getValue() && worst.getKey().getValue().isPresent())
                return CardState.DONE;
            else if (doubleEnginesPower != 0)
                return CardState.WAIT_ENGINE;
            else { // User cannon activate double engines, update worst
                worst.getKey().setValue(Optional.of(player));
                worst.setValue(freeEnginesPower);
                return CardState.DONE;
            }
        }
    };

    public abstract CardState countCriteria(PlayerData player, SimpleEntry<SimpleEntry<Character, Optional<PlayerData>>, Double> worst) throws Exception;
}
