package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

/**
 * Command implementation for rolling two dice in the game.
 * This command simulates the rolling of two six-sided dice and uses the sum
 * of their values as input for card effects that depend on dice roll outcomes.
 * <p>
 * The dice roll is performed during command construction, generating a random
 * value between 2 and 12 (inclusive) that represents the sum of two dice.
 * This value is then used when executing the card's command effects.
 */
public class RollDicesCommand implements Command {

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * The username of the player executing the dice roll command
     */
    private final String username;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * The sum of the two dice rolls, ranging from 2 to 12
     */
    private final int value;

    /**
     * Constructs a new RollDicesCommand and immediately performs the dice roll.
     * The dice roll is simulated by generating two random numbers between 1 and 6
     * (inclusive) and summing them to produce a value between 2 and 12.
     *
     * @param model    the model facade providing access to game state
     * @param board    the game board containing all game entities
     * @param username the username of the player executing the command
     */
    public RollDicesCommand(ModelFacade model, Board board, String username) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.value = ((int) (Math.random() * 6) + 1) + ((int) (Math.random() * 6) + 1);
    }

    /**
     * Executes the dice roll command by applying the card's command effects
     * using the pre-calculated dice roll value.
     * <p>
     * This method is straightforward as the dice rolling has already been performed
     * during construction. It simply passes the dice roll result to the card's
     * command effects method along with the appropriate player state.
     *
     * @param card the card being executed that triggered this command
     * @return true if the command effects were successfully applied based on the dice roll, false otherwise
     */
    @Override
    public boolean execute(Card card) {
        return card.doCommandEffects(PlayerState.WAIT_ROLL_DICES, value, model, board, username);
    }

}