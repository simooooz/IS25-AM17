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
 * Combat zone penalties can range from simple resource deductions to complex
 * multi-stage punishment sequences like cannon fire bombardments. Each penalty
 * type implements its own resolution logic while conforming to a common interface
 * that allows the combat zone system to handle them uniformly.
 * <p>
 * The class supports JSON serialization with type information to preserve
 * the specific penalty implementation when saving/loading game state.
 * The penalty system uses the command pattern to handle different types of
 * player interactions required during penalty resolution.
 *
 * @author Generated Javadoc
 * @version 1.0
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
     * This method is called when a player has been identified as the worst
     * performer in a combat zone evaluation and must face the consequences.
     * Each penalty implementation defines how the punishment should begin,
     * whether through immediate effects or by prompting for player interaction.
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
     * This method handles player actions that involve integer values, such as
     * dice rolls for determining impact coordinates in cannon fire penalties.
     * The base implementation throws an exception, requiring penalty types
     * that support integer commands to override this method.
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
     * This method handles player actions that involve boolean choices, such as
     * shield activation decisions in defensive scenarios. The base implementation
     * throws an exception, requiring penalty types that support boolean commands
     * to override this method.
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
     * This method handles player actions that don't require additional parameters,
     * such as completing ship part replacements after component destruction.
     * The base implementation throws an exception, requiring penalty types that
     * support parameterless commands to override this method.
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
     * This method provides access to quantifiable aspects of penalties, such as
     * the number of crew members to lose or goods to surrender. The base
     * implementation throws an exception, requiring penalty types with countable
     * effects to override this method.
     * <p>
     * The JsonIgnore annotation prevents this computed value from being serialized,
     * as it should be derived from the penalty's internal state rather than stored.
     *
     * @return the numerical value representing the penalty's magnitude
     * @throws RuntimeException if the penalty type doesn't have a quantifiable aspect
     */
    @JsonIgnore
    public int getPenaltyNumber() {
        throw new RuntimeException("Method not valid");
    }

}