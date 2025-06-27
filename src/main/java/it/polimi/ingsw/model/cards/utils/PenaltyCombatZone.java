package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

/**
 * Abstract base class for penalty implementations used in combat zone encounters.
 * This class defines the contract for applying punishments to players who perform
 * worst in combat evaluations, providing a polymorphic system for different penalty types.
 * <p>
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CannonFirePenaltyCombatZone.class, name = "CANNON_FIRE"),
        @JsonSubTypes.Type(value = CountablePenaltyZone.class, name = "COUNTABLE"),
})
public abstract class PenaltyCombatZone {

    /**
     * Constructs a new PenaltyCombatZone.
     * This default constructor is required for JSON deserialization and
     * provides a base for penalty implementations.
     */
    public PenaltyCombatZone() {
    }

    /**
     * Initiates the penalty resolution process for the specified player.
     * <p>
     *
     * @param model    the model facade providing access to game state
     * @param board    the game board containing the penalized player
     * @param username the username of the player receiving the penalty
     * @return the PlayerState indicating how the penalty should proceed
     */
    public abstract PlayerState resolve(ModelFacade model, Board board, String username);

    /**
     * Processes command effects with integer parameters during penalty resolution.
     * <p>
     *
     * @param commandType the type of command being executed
     * @param value       the integer value associated with the command
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player executing the command
     * @throws RuntimeException if the penalty type doesn't support integer commands
     */
    public void doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Processes command effects with boolean parameters during penalty resolution.
     * <p>
     *
     * @param commandType the type of command being executed
     * @param value       the boolean value indicating the player's choice
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player executing the command
     * @throws RuntimeException if the penalty type doesn't support boolean commands
     */
    public void doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Processes command effects without parameters during penalty resolution.
     * <p>
     *
     * @param commandType the type of command being executed
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player executing the command
     * @throws RuntimeException if the penalty type doesn't support parameterless commands
     */
    public void doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    /**
     * Retrieves the numerical value associated with this penalty, if applicable.
     * <p>
     *
     * @return the numerical value representing the penalty's magnitude
     * @throws RuntimeException if the penalty type doesn't have a quantifiable aspect
     */
    @JsonIgnore
    public int getPenaltyNumber() {
        throw new RuntimeException("Method not valid");
    }

}