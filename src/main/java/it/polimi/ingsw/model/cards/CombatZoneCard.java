package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.Pair;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.WarLine;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Card implementation representing a combat zone encounter in the game.
 * This card orchestrates multi-player combat scenarios where players compete
 * against each other across multiple war lines, with the worst-performing
 * player in each line receiving penalties.
 * <p>
 * The combat zone processes players through a series of war lines, each containing
 * specific criteria (engines, cannons, etc.) and associated penalties. Players
 * are evaluated based on their performance, and the player with the lowest score
 * on each war line must face the penalty consequences.
 * <p>
 * The card supports various command types including engine power, cannon power,
 * shield activation, dice rolling, crew removal, goods removal, and ship part penalties.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CombatZoneCard extends Card {

    /**
     * List of war lines that define the combat criteria and penalties
     */
    @JsonProperty
    private final List<WarLine> warLines;

    /**
     * Current index of the war line being processed
     */
    @JsonProperty
    private int warLineIndex;

    /**
     * Pair tracking the worst-performing player and their score for the current war line
     */
    @JsonProperty
    private final Pair<Optional<String>, Double> worst;

    /**
     * Current index of the player being processed in the war line
     */
    private int playerIndex;

    /**
     * List of players participating in the combat zone
     */
    private List<PlayerData> players;

    /**
     * Constructs a new CombatZoneCard with the specified parameters.
     * Initializes the worst-performing player tracker with empty values.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     * @param warLines  the list of war lines defining combat criteria and penalties
     */
    public CombatZoneCard(int id, int level, boolean isLearner, List<WarLine> warLines) {
        super(id, level, isLearner);
        this.warLines = warLines;
        this.worst = new Pair<>(Optional.empty(), 0.0);
    }

    /**
     * Starts the combat zone card execution by initializing player states and beginning
     * the first war line evaluation.
     * <p>
     * If fewer than 2 players are present, the card completes immediately as combat
     * requires multiple participants. Otherwise, initializes the combat state and
     * begins automatic player evaluation for the first war line.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the card execution is complete, false if players need to take actions
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        if (board.getPlayersByPos().size() < 2)
            return true;
        else {
            this.warLineIndex = 0;
            this.playerIndex = 0;
            this.players = new ArrayList<>(board.getPlayersByPos());

            for (PlayerData player : players)
                model.setPlayerState(player.getUsername(), PlayerState.WAIT);

            return autoCheckPlayers(model, board);
        }
    }

    /**
     * Automatically evaluates players against the current war line criteria and manages
     * the progression through war lines and penalty application.
     * <p>
     * This method performs the core combat zone logic:
     * 1. Evaluates each player against the current war line criteria
     * 2. Sets appropriate player states based on evaluation results
     * 3. Applies penalties to the worst-performing player when all players are done
     * 4. Progresses to the next war line or completes the card when all lines are finished
     * 5. Handles recursive progression through multiple war lines
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the combat zone card execution is complete, false if waiting for player actions
     */
    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            PlayerState newState = warLines.get(warLineIndex).getCriteriaType().countCriteria(player, worst);
            model.setPlayerState(player.getUsername(), newState);
            if (newState != PlayerState.DONE)
                return false;
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && worst.getKey().isPresent()) { // Apply malus
            PlayerState newState = warLines.get(warLineIndex).getPenalty().resolve(model, board, worst.getKey().get());
            model.setPlayerState(worst.getKey().get(), newState);

            // Update players because flight days could be changed (due to penalty)
            this.players = new ArrayList<>(board.getPlayersByPos().stream().filter(oldP -> players.contains(oldP)).toList());

            if (newState == PlayerState.DONE)
                worst.setKey(Optional.empty());
        }

        if (hasDone && worst.getKey().isEmpty()) { // Malus already applied, go to the next line
            warLineIndex++;
            if (warLineIndex >= warLines.size())
                return true;
            else {
                for (PlayerData player : players)
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                this.playerIndex = 0;
                return autoCheckPlayers(model, board);
            }
        }
        return false;
    }

    /**
     * Processes command effects for integer-based actions (engines, dice rolls).
     * <p>
     * Handles engine power evaluation and dice roll penalty resolution:
     * - For engine commands: Updates worst performer tracking and progresses player evaluation
     * - For dice roll commands: Delegates to penalty resolution and manages state transitions
     *
     * @param commandType the type of command being executed
     * @param value       the integer value associated with the command (engine power or dice result)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the combat zone card execution progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not supported for integer values
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            model.setPlayerState(username, PlayerState.DONE);

            if (worst.getKey().isEmpty() || worst.getValue() > value) { // Update worst
                worst.setKey(Optional.of(username));
                worst.setValue(value.doubleValue());
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        } else if (commandType == PlayerState.WAIT_ROLL_DICES) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.setKey(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes command effects for boolean-based actions (shield activation).
     * <p>
     * Handles shield-related penalty resolution by delegating to the current war line's
     * penalty system and managing state transitions based on the shield effectiveness.
     *
     * @param commandType the type of command being executed
     * @param value       the boolean value indicating shield power state
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the combat zone card execution progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not supported for boolean values
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, value, model, board, username);

            if (model.getPlayerState(username) == PlayerState.DONE) // If nested doCommandEffect has put state to DONE
                worst.setKey(Optional.empty());

            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes command effects for double-based actions (cannon power).
     * <p>
     * Handles cannon power evaluation by tracking the worst-performing player
     * and progressing through the combat evaluation sequence.
     *
     * @param commandType the type of command being executed
     * @param value       the double value representing cannon power
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the combat zone card execution progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not supported for double values
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            model.setPlayerState(username, PlayerState.DONE);

            if (worst.getKey().isEmpty() || worst.getValue() > value) { // Update worst
                worst.setKey(Optional.of(username));
                worst.setValue(value);
            }

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes command effects for parameter-less actions (crew removal, goods removal, ship parts).
     * <p>
     * Handles various penalty-related actions:
     * - Crew/goods removal: Clears worst performer tracking and marks player as done
     * - Ship part penalties: Delegates to penalty resolution system
     *
     * @param commandType the type of command being executed
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if the combat zone card execution progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not supported for parameter-less execution
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            worst.setKey(Optional.empty());
            model.setPlayerState(username, PlayerState.DONE);

            return autoCheckPlayers(model, board);
        } else if (commandType == PlayerState.WAIT_SHIP_PART) {
            warLines.get(warLineIndex).getPenalty().doCommandEffects(commandType, model, board, username);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for crew removal commands.
     * <p>
     * Validates that the specified cabin components and removal count match the
     * penalty requirements defined in the current war line.
     *
     * @param commandType the command type being validated
     * @param cabins      the list of cabin components involved in crew removal
     * @param toRemove    the number of crew members to remove (overridden by penalty number)
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            int num = warLines.get(warLineIndex).getPenalty().getPenaltyNumber();
            super.doSpecificCheck(commandType, cabins, num, username, board);
        }
    }

    /**
     * Performs specific validation checks for goods removal commands.
     * <p>
     * Validates that the goods delta and battery usage match the penalty requirements
     * defined in the current war line.
     *
     * @param commandType the command type being validated
     * @param number      the number parameter (overridden by penalty number)
     * @param deltaGood   the map of goods changes by color type
     * @param batteries   the list of battery components being used
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType == PlayerState.WAIT_REMOVE_GOODS) {
            int num = warLines.get(warLineIndex).getPenalty().getPenaltyNumber();
            super.doSpecificCheck(commandType, num, deltaGood, batteries, username, board);
        }
    }

    /**
     * Handles the effects when a player leaves the game during combat zone execution.
     * <p>
     * Manages player list updates and state transitions when a player disconnects:
     * - Clears worst performer tracking if the leaving player was being penalized
     * - Updates player indices and list to maintain proper iteration
     * - Continues combat evaluation if the current player left
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing all players and entities
     * @param username the username of the player leaving the game
     * @return true if combat zone card execution should continue automatically, false otherwise
     */
    @SuppressWarnings("Duplicates")
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
        if (board.getPlayersByPos().size() < 2)
            return true;

        if ( // Player was the worst
                state == PlayerState.WAIT_REMOVE_CREW || state == PlayerState.WAIT_REMOVE_GOODS ||
                        state == PlayerState.WAIT_ROLL_DICES || state == PlayerState.WAIT_SHIP_PART ||
                        state == PlayerState.WAIT_SHIELD
        )
            worst.setKey(Optional.empty());

        PlayerData player = board.getPlayerEntityByUsername(username);
        int indexOfLeftPlayer = players.indexOf(player);

        if (playerIndex > indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            playerIndex--;
        } else if (playerIndex == indexOfLeftPlayer) {
            players.remove(indexOfLeftPlayer);
            return autoCheckPlayers(model, board);
        } else
            players.remove(indexOfLeftPlayer);

        return playerIndex == players.size() && autoCheckPlayers(model, board);
    }

}