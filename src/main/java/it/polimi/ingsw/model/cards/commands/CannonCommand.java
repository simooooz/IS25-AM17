package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Command implementation for executing cannon attacks in the game.
 * This command handles the firing of cannons using battery components as power sources,
 * calculating total cannon power and applying card effects.
 * <p>
 * The command validates that the specified batteries and cannons are valid components
 * on the player's ship and that there are sufficient battery charges to power the cannons.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CannonCommand implements Command {

    /**
     * The username of the player executing the cannon command
     */
    private final String username;

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * List of battery components to be used for powering the cannons
     */
    private final List<BatteryComponent> batteries;

    /**
     * List of cannon components to be fired
     */
    private final List<CannonComponent> cannons;

    /**
     * Constructs a new CannonCommand with the specified parameters.
     *
     * @param model     the model facade providing access to game state
     * @param board     the game board containing all game entities
     * @param username  the username of the player executing the command
     * @param batteries the list of battery components to power the cannons
     * @param cannons   the list of cannon components to be fired
     */
    public CannonCommand(ModelFacade model, Board board, String username, List<BatteryComponent> batteries, List<CannonComponent> cannons) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.batteries = batteries;
        this.cannons = cannons;
    }

    /**
     * Executes the cannon command by validating input, calculating total cannon power,
     * consuming battery charges, and applying card effects.
     * <p>
     * The method performs the following operations:
     * 1. Validates that all specified components are valid and available on the ship
     * 2. Performs card-specific validation for the cannon state
     * 3. Calculates total cannon power from single cannons, double cannons, and alien bonus
     * 4. Consumes battery charges from the specified battery components
     * 5. Applies the card's command effects with the calculated cannon power
     *
     * @param card the card being executed that triggered this command
     * @return true if the command effects were successfully applied, false otherwise
     * @throws ComponentNotValidException        if any specified component is not valid or not on the ship
     * @throws BatteryComponentNotValidException if there are insufficient battery charges
     * @throws RuntimeException                  if the number of batteries doesn't match the number of cannons
     */
    @Override
    public boolean execute(Card card) {
        Ship ship = this.board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);
        card.doSpecificCheck(PlayerState.WAIT_CANNONS, cannons, username, board);

        double singleCannonPower = ship.getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(CannonComponent::calcPower)
                .sum();
        double doubleCannonPower = cannons.stream().mapToDouble(CannonComponent::calcPower).sum();
        double userCannonPower = singleCannonPower + doubleCannonPower;
        if (userCannonPower > 0 && ship.getCannonAlien())
            userCannonPower += 2;

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(PlayerState.WAIT_CANNONS, userCannonPower, model, board, username);
    }

    /**
     * Validates the input components to ensure they are valid for the cannon command.
     * <p>
     * This method performs comprehensive validation including:
     * - Verifying that all battery components exist on the ship's dashboard at their specified positions
     * - Verifying that all cannon components exist on the ship's dashboard at their specified positions
     * - Ensuring that all specified cannon components are double cannons
     * - Checking that the number of batteries matches the number of cannons
     * - Validating that each battery component has sufficient charge for the operation
     * - Ensuring that no cannon components are duplicated in the list
     *
     * @param ship the player's ship containing the components to validate
     * @throws ComponentNotValidException        if any component is not valid, not on the ship,
     *                                           or if cannons are not double cannons, or if there are duplicate cannons
     * @throws BatteryComponentNotValidException if any battery component lacks sufficient charge
     * @throws RuntimeException                  if the number of batteries doesn't equal the number of cannons
     */
    private void checkInput(Ship ship) {
        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        for (CannonComponent component : cannons) {
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cannon component not valid");
            else if (!component.getIsDouble())
                throw new ComponentNotValidException("Cannon component " + component.getId() + "is not double");
        }

        if (batteries.size() != cannons.size())
            throw new RuntimeException("Inconsistent number of batteries");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new BatteryComponentNotValidException("Not enough batteries in a single component");

        if (cannons.size() != cannons.stream().distinct().count())
            throw new ComponentNotValidException("Duplicate cannons");
    }

}
