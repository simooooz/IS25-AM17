package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

/**
 * Penalty implementation for combat zone encounters that applies quantity-based
 * punishments to players who performed worst in combat evaluations.
 * <p>
 * This penalty type represents punishments that can be measured in discrete units,
 * such as crew members lost, goods confiscated, or credits deducted. The penalty
 * is defined by both a specific type of malus and the quantity of that penalty
 * to be applied.
 * <p>
 * Countable penalties are typically used for straightforward punishments that
 * can be expressed as "lose X crew members" or "surrender Y goods," providing
 * clear, quantifiable consequences for poor combat performance in combat zones.
 * <p>
 * The penalty resolution is delegated to the specific malus type, which handles
 * the implementation details of how that particular penalty should be applied
 * to the affected player.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CountablePenaltyZone extends PenaltyCombatZone {

    /**
     * The quantity or amount of the penalty to be applied
     */
    @JsonProperty
    private final int penaltyNumber;

    /**
     * The type of malus that defines how the penalty should be applied
     */
    @JsonProperty
    private final MalusType penaltyType;

    /**
     * Constructs a new CountablePenaltyZone with the specified penalty parameters.
     *
     * @param penaltyNumber the quantity or amount of the penalty to be applied
     * @param penaltyType   the type of malus that defines the nature of the penalty
     */
    public CountablePenaltyZone(int penaltyNumber, MalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    /**
     * Resolves the countable penalty by delegating to the specific malus type implementation.
     * <p>
     * The resolution process is handled by the malus type, which knows how to apply
     * the specific kind of penalty (crew loss, goods confiscation, etc.) with the
     * specified quantity to the affected player. This delegation pattern allows
     * different penalty types to be handled uniformly while maintaining their
     * specific implementation details.
     *
     * @param model    the model facade providing access to game state
     * @param board    the game board containing the penalized player
     * @param username the username of the player receiving the penalty
     * @return the PlayerState that the penalized player should enter to resolve the penalty
     */
    @Override
    public PlayerState resolve(ModelFacade model, Board board, String username) {
        return penaltyType.resolve(penaltyNumber, board, username);
    }

    /**
     * Retrieves the quantity of the penalty to be applied.
     * <p>
     * This accessor method allows external systems to query the penalty amount,
     * which is useful for validation, display, or logging purposes. The penalty
     * number represents the discrete quantity of whatever resource or asset
     * the player must surrender as punishment.
     *
     * @return the quantity or amount of the penalty
     */
    @Override
    public int getPenaltyNumber() {
        return penaltyNumber;
    }
}