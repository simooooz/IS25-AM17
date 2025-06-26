package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

/**
 * Command implementation for activating shield protection in the game.
 * This command handles the activation of defensive shields, powered
 * by a battery component.
 */
public class ShieldCommand implements Command {

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * The username of the player executing the shield command
     */
    private final String username;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * The battery component used to power the shield, or null for unpowered shield activation
     */
    private final BatteryComponent battery;

    /**
     * Constructs a new ShieldCommand with the specified parameters.
     *
     * @param model    the model facade providing access to game state
     * @param board    the game board containing all game entities
     * @param username the username of the player executing the command
     * @param battery  the battery component to power the shield, or null for unpowered activation
     */
    public ShieldCommand(ModelFacade model, Board board, String username, BatteryComponent battery) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.battery = battery;
    }

    /**
     * Executes the shield command by validating input, optionally consuming battery power,
     * and applying shield effects through the card.
     * <p>
     * The method performs the following operations:
     * 1. Validates that the battery component (if provided) is valid and has sufficient charge
     * 2. Consumes battery charge if a battery component is specified
     * 3. Applies the card's command effects with the shield activation state
     *
     * @param card the card being executed that triggered this command
     * @return true if the shield command effects were successfully applied, false otherwise
     * @throws ComponentNotValidException        if the battery component is not valid or not on the ship
     * @throws BatteryComponentNotValidException if the battery component has no charge remaining
     */
    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        if (battery != null)
            battery.useBattery(ship);

        return card.doCommandEffects(PlayerState.WAIT_SHIELD, battery != null, model, board, username);
    }

    /**
     * Validates the battery component (if provided) to ensure it is valid for shield activation.
     * <p>
     * This method performs validation only when a battery component is specified:
     * - Verifies that the battery component exists on the ship's dashboard at its specified position
     * - Checks that the battery component has at least one charge available for consumption
     * <p>
     * If no battery is provided (null), no validation is performed as unpowered shield
     * activation is a valid operation that doesn't require any components.
     *
     * @param ship the player's ship containing the battery component to validate
     * @throws ComponentNotValidException        if the battery component is not valid or not on the ship
     * @throws BatteryComponentNotValidException if the battery component has no charge remaining
     */
    private void checkInput(Ship ship) {
        if (battery == null) return;

        if (ship.getDashboard(battery.getY(), battery.getX()).isEmpty() || !ship.getDashboard(battery.getY(), battery.getX()).get().equals(battery))
            throw new ComponentNotValidException("Battery component not valid");

        if (battery.getBatteries() == 0)
            throw new BatteryComponentNotValidException("Not enough batteries");
    }

}