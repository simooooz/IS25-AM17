package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.*;

/**
 * Card implementation representing an open space navigation event in the game.
 * This card allows players to use their ship's engines to advance their position
 * along the flight path, with movement distance determined by total engine power.
 * <p>
 * The open space card evaluates each player's engine capabilities and provides
 * movement opportunities. Players with only single engines move automatically
 * based on their free engine power, while players with double engines can choose
 * to activate additional engines using batteries for greater movement.
 * <p>
 * Players who fail to activate any engines (resulting in zero movement power)
 * face the consequence of ending their flight prematurely (only for standard mode)
 */
public class OpenSpaceCard extends Card {

    /**
     * Current index of the player being evaluated for engine usage
     */
    private int playerIndex;

    /**
     * List of players participating in the open space navigation
     */
    private List<PlayerData> players;

    /**
     * Map tracking the total engine power activated by each player for movement calculation
     */
    private Map<String, Integer> enginesActivated = new HashMap<>();

    /**
     * Constructs a new OpenSpaceCard with the specified parameters.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     */
    public OpenSpaceCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    /**
     * Starts the open space card execution by initializing player states and beginning
     * automatic engine evaluation.
     * <p>
     * Resets the activation tracking, initializes all players to waiting state,
     * and begins the automatic evaluation process for each player's engine capabilities.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if all players can move automatically, false if some players need to make engine choices
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        playerIndex = 0;
        this.enginesActivated = new HashMap<>();
        this.players = new ArrayList<>(board.getPlayersByPos());

        players.forEach(player ->
                model.setPlayerState(player.getUsername(), PlayerState.WAIT)
        );
        return autoCheckPlayers(model, board);
    }

    /**
     * Automatically evaluates each player's engine capabilities and determines movement actions.
     * <p>
     * For each player, the method:
     * 1. Calculates single engine power (including potential alien engine bonus)
     * 2. Calculates maximum potential double engine power (limited by available batteries)
     * 3. Determines player action based on engine configuration:
     * - If no double engines available: automatically moves with single engine power
     * - If double engines available: prompts player to choose engine activation
     * 4. Once all players have acted, executes movement for all players simultaneously
     * <p>
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if all players have been evaluated and movement executed, false if waiting for player choices
     */
    public boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            int singleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToInt(EngineComponent::calcPower)
                    .sum();
            if (singleEnginesPower > 0 && player.getShip().getEngineAlien())
                singleEnginesPower += 2;

            int doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToInt(EngineComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToInt(Integer::intValue)
                    .sum();

            if (doubleEnginesPower != 0) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_ENGINES);
                return false;
            } else {
                enginesActivated.put(player.getUsername(), singleEnginesPower);
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            }
        }

        for (PlayerData player : players)
            board.movePlayer(player, enginesActivated.get(player.getUsername()));

        return true;
    }

    /**
     * Processes engine activation command effects from players with double engine options.
     * <p>
     * Records the total engine power chosen by the player (combining single engines,
     * selected double engines, and potential alien bonuses) and progresses the
     * evaluation to the next player or completes the movement phase.
     *
     * @param commandType the type of command being executed (must be WAIT_ENGINES)
     * @param power       the integer value representing total engine power activated by the player
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player executing the command
     * @return true if all players have been evaluated and movement executed, false if more players need to choose
     * @throws RuntimeException if the command type is not WAIT_ENGINES
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer power, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            model.setPlayerState(username, PlayerState.DONE);
            enginesActivated.put(username, power);

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Executes end-of-card effects, specifically handling players who failed to activate engines.
     * <p>
     * Players who recorded zero engine activation are considered stranded in open space
     * and must end their flight prematurely (only for standard mode).
     *
     * @param board the game board containing all players and entities
     */
    @Override
    public void endCard(Board board) {
        for (PlayerData player : board.getPlayersByPos())
            if (enginesActivated.get(player.getUsername()) == 0)
                player.endFlight();
        super.endCard(board);
    }

    /**
     * Handles the effects when a player leaves the game during open space navigation.
     * <p>
     * Manages player list updates and continues engine evaluation when appropriate:
     * - Updates player indices and list to maintain proper iteration
     * - Continues engine evaluation if the current player left
     * - Maintains navigation flow for remaining players
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if open space navigation should continue automatically, false otherwise
     */
    @SuppressWarnings("Duplicates")
    @Override
    public boolean doLeftGameEffects(PlayerState state, ModelFacade model, Board board, String username) {
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

        return false;
    }

}