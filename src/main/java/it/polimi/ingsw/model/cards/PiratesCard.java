package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Card implementation representing a pirate encounter in the game.
 * This card extends the basic enemy encounter mechanics with additional features
 * including reward choices for successful defenders and cannon fire attacks
 * against defeated players.
 * <p>
 * The pirate encounter follows a two-phase structure:
 * 1. Initial combat phase: Players attempt to defeat pirates using cannon firepower
 * 2. Pirate retaliation phase: Defeated players face cannon fire attacks from pirates
 * <p>
 * Players who successfully defeat the pirates can choose to claim rewards (credits)
 * or decline them. Players who fail to defeat the pirates
 * are subjected to a series of cannon fire attacks that can destroy ship components.
 * <p>
 * The encounter includes defensive options during the retaliation phase, allowing
 * defeated players to use shields to protect against incoming pirate cannon fire.
 */
public class PiratesCard extends EnemiesCard {

    /**
     * The number of credits offered as reward for defeating the pirates
     */
    @JsonProperty
    private final int credits;

    /**
     * List of cannon fire attacks that pirates will use against defeated players
     */
    @JsonProperty
    private final List<CannonFire> cannonFires;

    /**
     * List of usernames of players who were defeated by the pirates
     */
    @JsonProperty
    private final List<String> defeatedPlayers;

    /**
     * Current index of the cannon fire being processed in the retaliation phase
     */
    @JsonProperty
    private int cannonIndex;

    /**
     * List of coordinates determined by dice rolls for each cannon fire attack
     */
    @JsonProperty
    private List<Integer> coords;

    /**
     * Constructs a new PiratesCard with the specified parameters.
     * Initializes tracking lists for defeated players and cannon fire coordinates.
     *
     * @param id               the unique identifier of the card
     * @param level            the level of the card
     * @param isLearner        whether this card is for learner mode
     * @param piratesFirePower the firepower strength of the pirate forces
     * @param credits          the number of credits offered as reward for victory
     * @param days             the number of flight days this encounter represents
     * @param cannonFires      the list of cannon fire attacks pirates will use in retaliation
     */
    public PiratesCard(int id, int level, boolean isLearner, int piratesFirePower, int credits, int days, List<CannonFire> cannonFires) {
        super(id, level, isLearner, days, piratesFirePower);
        this.credits = credits;
        this.cannonFires = cannonFires;
        this.defeatedPlayers = new ArrayList<>();
        this.coords = new ArrayList<>();
    }

    /**
     * Starts the pirate encounter card execution by initializing the cannon fire index
     * and delegating to the parent enemy encounter logic.
     * <p>
     * Resets the cannon fire sequence and begins the initial combat evaluation
     * phase where players attempt to defeat the pirates.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the encounter is complete, false if players need to take combat actions
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.cannonIndex = 0;
        return super.startCard(model, board);
    }

    /**
     * Calculates whether the pirate encounter is complete, handling the transition
     * between combat and retaliation phases.
     * <p>
     * The completion logic follows these phases:
     * 1. If players are still in combat and no one was defeated: encounter complete
     * 2. If all players finished combat but some were defeated: begin retaliation phase
     * 3. If all cannon fires completed: encounter complete
     * 4. If retaliation ongoing: continue with next cannon fire sequence
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the pirate encounter is complete, false if retaliation phase continues
     */
    @Override
    public boolean calcHasDone(ModelFacade model, Board board) {
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && defeatedPlayers.isEmpty())
            return true;
        else if (hasDone) {
            if (cannonIndex >= cannonFires.size())
                return true;
            else {
                for (String username : defeatedPlayers)
                    model.setPlayerState(username, PlayerState.WAIT);
                model.setPlayerState(defeatedPlayers.getFirst(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    /**
     * Handles the penalty for players defeated by the pirates by adding them
     * to the defeated players list for the retaliation phase.
     * <p>
     * Defeated players will face cannon fire attacks from the pirates as punishment
     * for their failure to defend against the pirate assault.
     *
     * @param model  the model facade providing access to game state
     * @param player the player data for the defeated player
     * @return false as defeated players don't require immediate state waiting
     */
    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        defeatedPlayers.add(player.getUsername());
        model.setPlayerState(player.getUsername(), PlayerState.DONE);
        return false;
    }

    /**
     * Processes dice roll command effects to determine pirate cannon fire coordinates
     * and trigger defensive responses.
     * <p>
     * When a dice roll is completed during the retaliation phase:
     * 1. Records the rolled coordinates for the current cannon fire
     * 2. Determines each defeated player's required defensive response
     * 3. Sets appropriate player states based on cannon fire impact
     * 4. Advances to the next cannon fire in the sequence
     *
     * @param commandType the type of command being executed (must be WAIT_ROLL_DICES)
     * @param value       the integer result of the dice roll determining cannon fire coordinates
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player who rolled the dice
     * @return true if the pirate encounter progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_ROLL_DICES
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            this.coords.add(value);
            for (String defeatedPlayerUsername : defeatedPlayers) {
                PlayerData defeatedPlayer = board.getPlayerEntityByUsername(defeatedPlayerUsername);
                PlayerState newState = cannonFires.get(cannonIndex).hit(defeatedPlayer, coords.getLast());
                model.setPlayerState(defeatedPlayerUsername, newState);
            }
            cannonIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes cannon-based combat command effects during the initial combat phase.
     * <p>
     * Handles cannon combat resolution with pirate-specific logic:
     * - If cannon power exceeds pirate firepower and pirates not yet defeated: offers reward choice
     * - If cannon power meets or exceeds pirate firepower: marks player as successful
     * - If cannon power is insufficient: adds player to defeated list for retaliation phase
     *
     * @param commandType the type of command being executed (must be WAIT_CANNONS)
     * @param value       the double value representing total cannon firepower used
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the encounter progresses automatically, false if waiting for player actions
     * @throws RuntimeException if the command type is not WAIT_CANNONS
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > enemyFirePower && !enemiesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
                return false;
            } else if (value >= enemyFirePower) // Tie or pirates already defeated
                model.setPlayerState(username, PlayerState.DONE);
            else { // Player is defeated
                defeatedPlayers.add(username);
                model.setPlayerState(username, PlayerState.DONE);
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes boolean-based command effects for reward choices and shield activation.
     * <p>
     * Handles two types of boolean commands:
     * 1. Reward choice (WAIT_BOOLEAN): Player decides whether to claim victory rewards
     * - If accepted: player receives credits and loses flight days
     * - If declined: player receives no rewards but avoids potential risks
     * 2. Shield activation (WAIT_SHIELD): Player attempts to defend against cannon fire
     * - If shield activated: player is protected from damage
     * - If shield not activated: component destruction may occur
     *
     * @param commandType the type of command being executed (WAIT_BOOLEAN or WAIT_SHIELD)
     * @param value       the boolean value indicating player choice or shield activation state
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not supported for boolean values
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                board.movePlayer(player, -1 * days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        } else if (commandType == PlayerState.WAIT_SHIELD) {
            if (value) // Shield activated
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // Not activated => find target and if present calc new state
                Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coords.getLast());
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(player.getUsername(), newState);
                });
            }
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
     * @return true if the pirate encounter progresses or completes, false otherwise
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
     * Handles the effects when a player leaves the game during pirate encounter execution.
     * <p>
     * Manages the continuation of the pirate encounter when players disconnect:
     * - Removes the leaving player from the defeated players list
     * - If the leaving player was supposed to roll dice, assigns dice rolling to the next defeated player
     * - Continues encounter evaluation using parent class logic for combat phase management
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if the pirate encounter should continue automatically, false otherwise
     */
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        defeatedPlayers.remove(username);
        if (state == PlayerState.WAIT_ROLL_DICES) {
            if (!defeatedPlayers.isEmpty())
                model.setPlayerState(defeatedPlayers.getFirst(), PlayerState.WAIT_ROLL_DICES);
        }

        return super.doLeftGameEffects(state, model, board, username);
    }

}