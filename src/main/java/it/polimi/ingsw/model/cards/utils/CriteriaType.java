package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.common.model.Pair;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.Comparator;
import java.util.Optional;

/**
 * Enumeration defining different criteria types used for evaluating player performance
 * in combat zone encounters and determining the worst-performing player for penalty application.
 * <p>
 */
public enum CriteriaType {

    /**
     * Crew criteria evaluates players based on their total crew count.
    */
    CREW {
        /**
         * Evaluates a player's crew count against the current worst performer.
         * <p>
         * Compares the player's total crew count with the current worst value,
         * updating the worst performer if this player has fewer crew members.
         * Crew evaluation always completes immediately as it requires no player action.
         *
         * @param player the player data containing ship and crew information
         * @param worst the pair tracking the current worst performer and their crew count
         * @return PlayerState.DONE as crew evaluation requires no player interaction
         */
        @Override
        public PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst) {
            if (player.getShip().getCrew() < worst.getValue() || worst.getKey().isEmpty()) {
                worst.setKey(Optional.of(player.getUsername()));
                worst.setValue((double) player.getShip().getCrew());
            }
            return PlayerState.DONE;
        }
    },

    /**
     * Cannon criteria evaluates players based on their available firepower.
     */
    CANNON {
        /**
         * Evaluates a player's cannon firepower against the current worst performer.
         * <p>
         * Players with sufficient free cannon power compared to the current worst are
         * automatically marked as done. Players with potential double cannon improvements
         * are prompted to choose their cannon activation strategy.
         *
         * @param player the player data containing ship and cannon information
         * @param worst the pair tracking the current worst performer and their cannon power
         * @return PlayerState.DONE if no action needed, PlayerState.WAIT_CANNONS if choice required
         */
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

    /**
     * Engine criteria evaluates players based on their available propulsion power.
     * This represents the mobility capability for movement and positioning,
     * considering both single engines (always available) and double engines
     * (requiring battery power to activate).
     * <p>
     * Engine evaluation mirrors the cannon system but focuses on movement
     * capabilities rather than combat power, providing similar strategic
     * choices about battery usage for enhanced performance.
     */
    ENGINE {
        /**
         * Evaluates a player's engine power against the current worst performer.
         * <p>
         * The evaluation process:
         * 1. Calculates free engine power from single engines (plus alien bonus if applicable)
         * 2. Calculates potential double engine power (limited by available batteries)
         * 3. Determines if the player can improve their standing with double engine activation
         * 4. Either marks them as done or prompts for engine selection based on capabilities
         * <p>
         * Players with sufficient free engine power compared to the current worst are
         * automatically marked as done. Players with potential double engine improvements
         * are prompted to choose their engine activation strategy.
         *
         * @param player the player data containing ship and engine information
         * @param worst the pair tracking the current worst performer and their engine power
         * @return PlayerState.DONE if no action needed, PlayerState.WAIT_ENGINES if choice required
         */
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

    /**
     * Abstract method that must be implemented by each criteria type to define
     * how player evaluation should be performed for that specific criterion.
     * <p>
     *
     * @param player the player data to evaluate against the criterion
     * @param worst  the pair tracking the current worst performer and their score
     * @return the PlayerState indicating what action the player should take next
     */
    public abstract PlayerState countCriteria(PlayerData player, Pair<Optional<String>, Double> worst);
}