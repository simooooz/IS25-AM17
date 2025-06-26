package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;

/**
 * Command interface that defines the contract for executing game actions triggered by cards.
 * <p>
 * This interface follows the Command design pattern, allowing different types of game actions
 * to be encapsulated as objects that can be executed uniformly. Each implementation represents
 * a specific type of action that can be performed in the game, such as movement, attacks, or
 * special abilities.
 * <p>
 * Implementations of this interface should handle all the necessary validation, state changes,
 * and side effects required for their specific action type.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public interface Command {

    /**
     * Executes the command with the specified card context.
     * <p>
     * This method is responsible for performing the action represented by this command.
     * The implementation should validate any preconditions, apply the necessary changes
     * to the game state, and handle any exceptions or error conditions that may arise
     * during execution.
     *
     * @param card the card that triggered this command execution, providing context
     *             and potentially additional effects or validation rules
     * @return true if the command was executed successfully and its effects were applied,
     * false if the command failed to execute or its effects could not be applied
     * @throws RuntimeException or its subclasses if critical validation fails or
     *                          if the command encounters an unrecoverable error during execution
     */
    boolean execute(Card card);

}