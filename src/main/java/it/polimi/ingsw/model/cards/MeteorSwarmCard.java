package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Card implementation representing a meteor swarm encounter in the game.
 * This card simulates a series of meteor impacts that threaten all players' ships,
 * requiring defensive actions such as cannon fire or shield activation to avoid damage.
 * <p>
 * The meteor swarm consists of multiple meteors that strike sequentially, each with
 * specific trajectory patterns and target zones. Players can defend against meteors
 * using cannons (if properly positioned and oriented) or shields (powered or unpowered).
 * Meteors that are not successfully defended against will destroy ship components.
 * <p>
 * The card uses dice rolls to determine the specific coordinates where each meteor
 * will impact.
 */
public class MeteorSwarmCard extends Card {

    /**
     * List of meteors that will impact during this swarm event
     */
    @JsonProperty
    private final List<Meteor> meteors;

    /**
     * Current index of the meteor being processed in the swarm sequence
     */
    @JsonProperty
    private int meteorIndex;

    /**
     * List of coordinates determined by dice rolls for each meteor impact
     */
    @JsonProperty
    private List<Integer> coords;

    /**
     * Constructs a new MeteorSwarmCard with the specified parameters.
     * Initializes the coordinates list to track meteor impact locations.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     * @param meteors   the list of meteors that will impact during this swarm
     */
    public MeteorSwarmCard(int id, int level, boolean isLearner, List<Meteor> meteors) {
        super(id, level, isLearner);
        this.meteors = meteors;
        this.coords = new ArrayList<>();
    }

    /**
     * Starts the meteor swarm card execution by initializing the first meteor sequence.
     * <p>
     * Sets all players to waiting state and prompts the first player to roll dice
     * to determine the impact coordinates for the first meteor. The meteor swarm
     * will proceed sequentially through each meteor in the list.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return false as the meteor swarm requires player interaction (dice rolling)
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.meteorIndex = 0;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
        return false;
    }

    /**
     * Automatically checks if all players have completed their actions for the current meteor
     * and progresses to the next meteor or completes the swarm.
     * <p>
     * When all players have finished responding to the current meteor:
     * 1. Advances to the next meteor in the sequence
     * 2. If all meteors have been processed, completes the card
     * 3. If more meteors remain, resets player states and initiates dice roll for next meteor
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the meteor swarm is complete, false if more meteors remain
     */
    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            meteorIndex++;
            if (meteorIndex >= meteors.size())
                return true;
            else {
                for (PlayerData player : board.getPlayersByPos())
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    /**
     * Performs specific validation checks for cannon-based meteor defense commands.
     * <p>
     * Validates that:
     * - Only one cannon component is provided (meteors require focused fire)
     * - The cannon is properly positioned to target the meteor's impact zone
     * - The cannon is oriented in the correct direction to intercept the meteor
     * - The cannon is within the valid target area for the current meteor and coordinates
     *
     * @param commandType the command type being validated (must be WAIT_CANNONS)
     * @param cannons     the list of cannon components to use for defense (must contain exactly 0 or 1 cannon)
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     * @throws IllegalArgumentException if more than one cannon is provided or if the cannon
     *                                  is not properly positioned/oriented for meteor interception
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, List<CannonComponent> cannons, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (cannons.size() > 1) throw new IllegalArgumentException("Too many cannon components provided");
            if (cannons.isEmpty()) return;

            CannonComponent chosenCannon = cannons.getFirst();

            List<Component> targets = meteors.get(meteorIndex).getTargets(player.getShip(), coords.getLast());
            if (meteors.get(meteorIndex).getDirectionFrom() != DirectionType.NORTH) {
                targets.addAll(meteors.get(meteorIndex).getDirectionFrom().getComponentsFromThisDirection(player.getShip().getDashboard(), coords.getLast() - 1));
                targets.addAll(meteors.get(meteorIndex).getDirectionFrom().getComponentsFromThisDirection(player.getShip().getDashboard(), coords.getLast() + 1));
            }

            if (chosenCannon.getDirection() != meteors.get(meteorIndex).getDirectionFrom() || !targets.contains(chosenCannon))
                throw new IllegalArgumentException("Cannon component not found in target coordinates");
        }
    }

    /**
     * Processes dice roll command effects to determine meteor impact coordinates and trigger defenses.
     * <p>
     * When a dice roll is completed:
     * 1. Records the rolled coordinates for the current meteor
     * 2. Determines each player's required defensive response based on meteor impact
     * 3. Sets appropriate player states (DONE if unaffected, defensive actions if threatened)
     * 4. Progresses the meteor swarm sequence
     *
     * @param commandType the type of command being executed (must be WAIT_ROLL_DICES)
     * @param value       the integer result of the dice roll determining impact coordinates
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player who rolled the dice
     * @return true if the meteor swarm progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_ROLL_DICES
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            this.coords.add(value);
            for (PlayerData player : board.getPlayersByPos()) {
                PlayerState newState = meteors.get(meteorIndex).hit(player, coords.getLast());
                model.setPlayerState(player.getUsername(), newState);
            }
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes cannon-based defense command effects against meteors.
     * <p>
     * Resolves cannon defense attempts:
     * - If cannon power is 0 (failed defense): destroys the first available target component
     * - If cannon power is > 0 (successful defense): meteor is destroyed, player is safe
     * <p>
     * Component destruction may trigger ship part replacement requirements.
     *
     * @param commandType the type of command being executed (must be WAIT_CANNONS)
     * @param value       the double value representing cannon firepower used in defense
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the meteor swarm progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_CANNONS
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value == 0) {
                Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coords.getLast()).stream().findFirst();
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(username, newState);
                });
            } else
                model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes shield-based defense command effects against meteors.
     * <p>
     * Resolves shield defense attempts:
     * - If shield is unpowered (false): destroys the first available target component
     * - If shield is powered (true): meteor impact is deflected, player is safe
     * <p>
     * Component destruction may trigger ship part replacement requirements.
     *
     * @param commandType the type of command being executed (must be WAIT_SHIELD)
     * @param value       the boolean value indicating whether shield was powered
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the meteor swarm progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_SHIELD
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_SHIELD) {
            if (!value) {
                Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coords.getLast()).stream().findFirst();
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(username, newState);
                });
            } else
                model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes the effects of the command to choose the part of the ship to be retained
     * following the destruction of components and the separation of the ship into two or more parts
     * <p>
     * @param commandType the type of command being executed (must be WAIT_SHIP_PART)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing ship part replacement
     * @return true if the meteor swarm progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_SHIP_PART
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Handles the effects when a player leaves the game during meteor swarm execution.
     * <p>
     * Manages the continuation of meteor swarm when players disconnect:
     * - If the leaving player was supposed to roll dice, assigns dice rolling to the next player
     * - Continues meteor sequence evaluation for remaining players
     * - Completes the card if no players remain
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if the meteor swarm should complete, false if it continues with remaining players
     */
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        if (board.getPlayersByPos().isEmpty())
            return true;

        if (state == PlayerState.WAIT_ROLL_DICES) {
            model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
        }
        return autoCheckPlayers(model, board);
    }

}