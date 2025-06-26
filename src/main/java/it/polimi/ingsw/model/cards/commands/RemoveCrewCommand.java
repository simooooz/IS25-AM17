package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Command implementation for removing crew members from cabin components in the game.
 * This command handles the removal of both human crew members and alien crew members
 * from specified cabin components on the player's ship.
 * <p>
 * It validates that the specified cabins exist
 * on the ship and contain sufficient crew members to be removed.
 */
public class RemoveCrewCommand implements Command {

    /**
     * The model facade providing access to game state
     */
    private final ModelFacade model;

    /**
     * The username of the player executing the remove crew command
     */
    private final String username;

    /**
     * The game board containing all game entities
     */
    private final Board board;

    /**
     * List of cabin components from which crew members will be removed
     */
    private final List<CabinComponent> cabinComponents;

    /**
     * Constructs a new RemoveCrewCommand with the specified parameters.
     *
     * @param model           the model facade providing access to game state
     * @param board           the game board containing all game entities
     * @param username        the username of the player executing the command
     * @param cabinComponents the list of cabin components from which to remove crew members
     */
    public RemoveCrewCommand(ModelFacade model, Board board, String username, List<CabinComponent> cabinComponents) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.cabinComponents = cabinComponents;
    }

    /**
     * Executes the remove crew command by validating input, performing card-specific checks,
     * and removing crew members from the specified cabins.
     * <p>
     * The method performs the following operations:
     * 1. Validates that all specified cabin components are valid and exist on the ship
     * 2. Performs card-specific validation for the crew removal state
     * 3. Removes crew members from each cabin
     * 4. Applies the card's command effects for the crew removal operation
     *
     * @param card the card being executed that triggered this command
     * @return true if the command effects were successfully applied, false otherwise
     * @throws ComponentNotValidException      if any specified cabin component is not valid or not on the ship
     * @throws CabinComponentNotValidException if any cabin lacks sufficient crew members to remove
     */
    @Override
    public boolean execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);
        card.doSpecificCheck(PlayerState.WAIT_REMOVE_CREW, cabinComponents, 0, username, board);

        for (CabinComponent cabin : cabinComponents) {
            if (cabin.getAlien().isPresent())
                cabin.setAlien(null, ship);
            else
                cabin.setHumans(cabin.getHumans() - 1, ship);
        }

        return card.doCommandEffects(PlayerState.WAIT_REMOVE_CREW, model, board, username);
    }

    /**
     * Validates the input cabin components to ensure they are valid for the crew removal command.
     * <p>
     * This method performs comprehensive validation including:
     * - Verifying that all cabin components exist on the ship's dashboard at their specified positions
     * - Checking that each cabin has sufficient crew members to accommodate the removal operation
     * - For cabins with aliens: validates that the removal count doesn't exceed 1 (since only one alien can be present)
     * - For cabins with only humans: validates that the removal count doesn't exceed the current human count
     * - Handles duplicate cabin references by ensuring total removals per cabin don't exceed available crew
     *
     * @param ship the player's ship containing the cabin components to validate
     * @throws ComponentNotValidException      if any cabin component is not valid or not on the ship
     * @throws CabinComponentNotValidException if any cabin lacks sufficient crew members for the removal operation
     */
    private void checkInput(Ship ship) {
        for (CabinComponent component : cabinComponents)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cabin component not valid");

        boolean enoughCrew = cabinComponents.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= (entry.getKey().getAlien().isPresent() ? 1 : entry.getKey().getHumans()));
        if (!enoughCrew)
            throw new CabinComponentNotValidException("Not enough crew in a cabin");
    }

}