package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Penalty implementation for combat zone encounters that subjects players to
 * a sequence of cannon fire attacks as punishment for poor performance.
 * <p>
 */
public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {

    /**
     * List of cannon fire attacks that comprise this penalty sequence
     */
    @JsonProperty
    private final List<CannonFire> cannonFires;

    /**
     * List of coordinates determined by dice rolls for each cannon fire attack
     */
    @JsonProperty
    private List<Integer> coords;

    /**
     * Current index of the cannon fire being processed in the penalty sequence
     */
    @JsonProperty
    private int cannonIndex;

    /**
     * Constructs a new CannonFirePenaltyCombatZone with the specified cannon fire sequence.
     * Initializes the coordinates tracking list for impact locations.
     *
     * @param cannonFires the list of cannon fire attacks that comprise this penalty
     */
    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
        this.coords = new ArrayList<>();
    }

    /**
     * Initiates the cannon fire penalty sequence by prompting for the first dice roll.
     *
     * @param model    the model facade providing access to game state
     * @param board    the game board containing the penalized player
     * @param username the username of the player receiving the penalty
     * @return PlayerState.WAIT_ROLL_DICES to initiate the first cannon fire attack
     */
    @Override
    public PlayerState resolve(ModelFacade model, Board board, String username) {
        return PlayerState.WAIT_ROLL_DICES;
    }

    /**
     * Processes dice roll command effects to determine cannon fire impact coordinates
     * and execute attack resolution.
     *
     * @param commandType the type of command being executed (must be WAIT_ROLL_DICES)
     * @param value       the integer result of the dice roll determining impact coordinates
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player receiving the penalty
     * @throws RuntimeException if the command type is not WAIT_ROLL_DICES
     */
    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            this.coords.add(value);

            PlayerState newState = cannonFires.get(cannonIndex).hit(player, coords.getLast());
            if (newState == PlayerState.DONE && cannonIndex < cannonFires.size() - 1) { // Cannot go in done if it's not really finished because is a deeper state.
                newState = PlayerState.WAIT_ROLL_DICES;
                cannonIndex++;
            }

            model.setPlayerState(username, newState);
        } else
            throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes shield activation command effects for defensive responses to cannon fire.
     *
     * @param commandType the type of command being executed (must be WAIT_SHIELD)
     * @param value       the boolean value indicating whether shield was activated
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player receiving the penalty
     * @throws RuntimeException if the command type is not WAIT_SHIELD
     */
    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            PlayerData player = board.getPlayerEntityByUsername(username);

            if (!value) { // Component destroyed
                Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coords.getLast());
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(player.getUsername(), newState);
                });
            }

            if (model.getPlayerState(player.getUsername()) == PlayerState.WAIT_SHIP_PART) // Not finished yet, user has to choose part of ship to remove and then cannon index can be increased
                return;

            cannonIndex++;
            if (cannonIndex < cannonFires.size())
                model.setPlayerState(username, PlayerState.WAIT_ROLL_DICES);
            else
                model.setPlayerState(username, PlayerState.DONE);
        } else
            throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes ship part choice command effects following component destruction
     * and ship fragmentation and manages continuation of the cannon fire penalty sequence.
     *
     * @param commandType the type of command being executed (must be WAIT_SHIP_PART)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing the penalized player
     * @param username    the username of the player receiving the penalty
     * @throws RuntimeException if the command type is not WAIT_SHIP_PART
     */
    @Override
    public void doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            cannonIndex++;
            if (cannonIndex < cannonFires.size())
                model.setPlayerState(username, PlayerState.WAIT_ROLL_DICES);
            else
                model.setPlayerState(username, PlayerState.DONE);
        } else
            throw new RuntimeException("Command type not valid");
    }

}