package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Command implementation for executing engine-powered movement actions in the game.
 * This command handles the activation of engine components using battery components as power sources,
 * calculating total engine power and applying movement-related card effects.
 * <p>
 * The command validates that the specified batteries and engines are valid components
 * on the player's ship and that there are sufficient battery charges to power the engines.
 * Engine power is calculated from both single engines (automatically included) and
 * double engines (specified in the command parameters).
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class EngineCommand implements Command {

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * The username of the player executing the engine command
     */
    private final String username;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * List of battery components to be used for powering the engines
     */
    private final List<BatteryComponent> batteries;

    /**
     * List of engine components to be activated (must be double engines)
     */
    private final List<EngineComponent> engines;

    /**
     * Constructs a new EngineCommand with the specified parameters.
     *
     * @param model     the model facade providing access to game state
     * @param board     the game board containing all game entities
     * @param username  the username of the player executing the command
     * @param batteries the list of battery components to power the engines
     * @param engines   the list of engine components to be activated (must be double engines)
     */
    public EngineCommand(ModelFacade model, Board board, String username, List<BatteryComponent> batteries, List<EngineComponent> engines) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.batteries = batteries;
        this.engines = engines;
    }

    /**
     * Executes the engine command by validating input, calculating total engine power,
     * consuming battery charges, and applying card effects.
     * <p>
     * The method performs the following operations:
     * 1. Validates that all specified components are valid and available on the ship
     * 2. Calculates total engine power from single engines (count-based) and double engines (power-based)
     * 3. Applies alien engine bonus if the ship has engine alien technology and engine power > 0
     * 4. Consumes battery charges from the specified battery components
     * 5. Applies the card's command effects with the calculated engine power for movement
     *
     * @param card the card being executed that triggered this command
     * @return true if the command effects were successfully applied, false otherwise
     * @throws ComponentNotValidException        if any specified component is not valid or not on the ship
     * @throws BatteryComponentNotValidException if there are insufficient battery charges
     * @throws RuntimeException                  if the number of batteries doesn't match the number of engines
     */
    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        int singleEnginePower = ship.getComponentByType(EngineComponent.class).stream()
                .filter(engine -> !engine.getIsDouble())
                .toList().size();
        int doubleEnginePower = engines.stream().mapToInt(EngineComponent::calcPower).sum();
        int userEnginePower = singleEnginePower + doubleEnginePower;
        if (userEnginePower > 0 && ship.getEngineAlien())
            userEnginePower += 2;

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(PlayerState.WAIT_ENGINES, userEnginePower, model, board, username);
    }

    /**
     * Validates the input components to ensure they are valid for the engine command.
     * <p>
     * This method performs comprehensive validation including:
     * - Verifying that all battery components exist on the ship's dashboard at their specified positions
     * - Verifying that all engine components exist on the ship's dashboard at their specified positions
     * - Ensuring that all specified engine components are double engines (single engines are included automatically)
     * - Checking that the number of batteries matches the number of engines (1:1 ratio required)
     * - Validating that each battery component has sufficient charge for the operation
     * - Ensuring that no engine components are duplicated in the list
     *
     * @param ship the player's ship containing the components to validate
     * @throws ComponentNotValidException        if any component is not valid, not on the ship,
     *                                           or if engines are not double engines, or if there are duplicate engines
     * @throws BatteryComponentNotValidException if any battery component lacks sufficient charge
     * @throws RuntimeException                  if the number of batteries doesn't equal the number of engines
     */
    private void checkInput(Ship ship) {
        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        for (EngineComponent component : engines) {
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Engine component not valid");
            else if (!component.getIsDouble())
                throw new ComponentNotValidException("Engine component " + component.getId() + " is not double");
        }

        if (batteries.size() != engines.size())
            throw new RuntimeException("Inconsistent number of batteries");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new BatteryComponentNotValidException("Not enough batteries");

        if (engines.size() != engines.stream().distinct().count())
            throw new ComponentNotValidException("Duplicate engines");
    }

}