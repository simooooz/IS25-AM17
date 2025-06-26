package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Command implementation for managing goods in cargo holds within the game.
 * This command handles the loading, unloading, and rearrangement of goods in special cargo hold components,
 * using battery components as power sources for the cargo operations.
 * <p>
 * The command calculates the net change in goods (delta) by comparing the current state with the desired
 * new disposition of goods across all cargo holds. It validates cargo capacity constraints, special
 * good restrictions (like red goods requiring special cargo holds), and battery availability.
 */
public class GoodCommand implements Command {

    /**
     * The username of the player executing the good command
     */
    private final String username;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * Map tracking the net change in goods quantity for each color type
     */
    private final Map<ColorType, Integer> deltaGood;

    /**
     * Map defining the new disposition of goods in each special cargo hold component
     */
    private final Map<SpecialCargoHoldsComponent, List<ColorType>> newDisposition;

    /**
     * List of battery components to be used for powering the cargo operations
     */
    private final List<BatteryComponent> batteries;

    /**
     * Constructs a new GoodCommand with the specified parameters.
     * Initializes the deltaGood map with zero values for all color types.
     *
     * @param model          the model facade providing access to game state
     * @param board          the game board containing all game entities
     * @param username       the username of the player executing the command
     * @param newDisposition the desired new arrangement of goods in cargo holds
     * @param batteries      the list of battery components to power the cargo operations
     */
    public GoodCommand(ModelFacade model, Board board, String username, Map<SpecialCargoHoldsComponent, List<ColorType>> newDisposition, List<BatteryComponent> batteries) {
        this.username = username;
        this.board = board;
        this.model = model;
        this.deltaGood = new HashMap<>();
        for (ColorType c : ColorType.values()) {
            this.deltaGood.put(c, 0);
        }
        this.newDisposition = newDisposition;
        this.batteries = batteries;
    }

    /**
     * Executes the good command by validating input, calculating goods delta, performing cargo operations,
     * consuming battery charges, and applying card effects.
     * <p>
     * The method performs the following operations:
     * 1. Validates that all specified components are valid and cargo constraints are met
     * 2. Calculates the net change in goods (deltaGood) by comparing current and desired states
     * 3. Performs card-specific validation based on the current player state (WAIT_GOODS or WAIT_REMOVE_GOODS)
     * 4. Unloads all goods from existing cargo holds and reloads them according to the new disposition
     * 5. Consumes battery charges from the specified battery components
     * 6. Applies the card's command effects for the goods operation
     *
     * @param card the card being executed that triggered this command
     * @return true if the command effects were successfully applied, false otherwise
     * @throws ComponentNotValidException      if any specified component is not valid or not on the ship
     * @throws GoodNotValidException           if cargo capacity is exceeded or invalid good placements are attempted
     * @throws CabinComponentNotValidException if there are insufficient battery charges
     */
    @Override
    public boolean execute(Card card) {
        Ship ship = this.board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);

        // increases goods value for each good which is present after the call
        for (SpecialCargoHoldsComponent c : newDisposition.keySet())
            for (int i = 0; i < newDisposition.get(c).size(); i++)
                deltaGood.put(newDisposition.get(c).get(i), deltaGood.get(newDisposition.get(c).get(i)) + 1);

        // decreases goods value for each good which is present before the call
        for (ColorType good : ColorType.values())
            deltaGood.put(good, deltaGood.get(good) - ship.getGoods().get(good));

        if (model.getPlayerState(username) == PlayerState.WAIT_GOODS)
            card.doSpecificCheck(PlayerState.WAIT_GOODS, null, deltaGood, batteries, username, board);
        else
            card.doSpecificCheck(PlayerState.WAIT_REMOVE_GOODS, 0, deltaGood, batteries, username, board);

        List<SpecialCargoHoldsComponent> componentsInShip = ship.getComponentByType(SpecialCargoHoldsComponent.class);
        for (SpecialCargoHoldsComponent component : componentsInShip) {
            List<ColorType> currentGoods = new ArrayList<>(component.getGoods());
            for (ColorType good : currentGoods) {
                component.unloadGood(good, ship);
            }
            if (newDisposition.containsKey(component))
                for (ColorType good : newDisposition.get(component))
                    component.loadGood(good, ship);
        }

        batteries.forEach(batteryComponent -> batteryComponent.useBattery(ship));
        return card.doCommandEffects(model.getPlayerState(username), model, board, username);
    }

    /**
     * Validates the input components and cargo arrangements to ensure they are valid for the good command.
     * <p>
     * This method performs comprehensive validation including:
     * - Verifying that all cargo hold components exist on the ship's dashboard at their specified positions
     * - Checking that the number of goods assigned to each cargo hold doesn't exceed its capacity
     * - Ensuring that red goods are only placed in special cargo holds (not normal cargo holds)
     * - Verifying that all battery components exist on the ship's dashboard at their specified positions
     * - Validating that each battery component has sufficient charge for the operation
     *
     * @param ship the player's ship containing the components to validate
     * @throws ComponentNotValidException      if any cargo hold or battery component is not valid or not on the ship
     * @throws GoodNotValidException           if cargo capacity is exceeded or red goods are placed in normal holds
     * @throws CabinComponentNotValidException if any battery component lacks sufficient charge
     */
    private void checkInput(Ship ship) {
        for (Component component : newDisposition.keySet())
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cargo hold component not valid");

        for (SpecialCargoHoldsComponent component : newDisposition.keySet()) {
            if (newDisposition.get(component).size() > component.getNumber())
                throw new GoodNotValidException("Too many goods in cargo hold");
            if (newDisposition.get(component).contains(ColorType.RED) && !component.matchesType(SpecialCargoHoldsComponent.class))
                throw new GoodNotValidException("Red good in normal hold");
        }

        for (Component component : batteries)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Battery component not valid");

        boolean enoughBatteries = batteries.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= entry.getKey().getBatteries());
        if (!enoughBatteries)
            throw new CabinComponentNotValidException("Not enough batteries in a single component");
    }

}