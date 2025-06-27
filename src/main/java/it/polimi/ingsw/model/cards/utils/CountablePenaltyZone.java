package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

/**
 * Penalty implementation for combat zone encounters that applies quantity-based
 * punishments to players who performed worst in combat evaluations.
 * <p>
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
     *
     * @return the quantity or amount of the penalty
     */
    @Override
    public int getPenaltyNumber() {
        return penaltyNumber;
    }
}