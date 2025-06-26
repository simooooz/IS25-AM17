package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Card implementation representing a planetary encounter in the game.
 * This card allows players to land on available planets to gather resources
 * and trade goods, following a two-phase structure for planetary exploration.
 * <p>
 * The planetary encounter operates in two distinct phases:
 * 1. Landing phase: Players choose which planet to land on (if any) from available options
 * 2. Trading phase: Landed players conduct goods trading based on their chosen planet's rewards
 * <p>
 * Each planet can only accommodate one player, creating competition for the most
 * desirable landing sites. Players who land on planets lose flight days at the end of the encounter.
 */
public class PlanetCard extends Card {

    /**
     * List of available planets that players can choose to land on
     */
    @JsonProperty
    private final List<Planet> planets;

    /**
     * Map tracking which player has landed on which planet
     */
    @JsonProperty
    private final Map<String, Planet> landedPlayers;

    /**
     * The number of flight days players lose for landing on planets
     */
    @JsonProperty
    private final int days;

    /**
     * Current index of the player being processed in the landing phase
     */
    private int playerIndex;

    /**
     * List of players participating in the planetary encounter
     */
    private List<PlayerData> players;

    /**
     * Constructs a new PlanetCard with the specified parameters.
     * Initializes the landed players tracking map.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     * @param planets   the list of available planets for landing
     * @param days      the number of flight days players advance for landing
     */
    public PlanetCard(int id, int level, boolean isLearner, List<Planet> planets, int days) {
        super(id, level, isLearner);
        this.planets = planets;
        this.days = days;
        this.landedPlayers = new HashMap<>();
    }

    /**
     * Starts the planetary encounter card execution by initializing player states
     * and beginning the landing phase.
     * <p>
     * Sets all players to waiting state and begins the automatic evaluation
     * process for planet selection in player order.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the encounter is complete, false if players need to make planet choices
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model, board);
    }

    /**
     * Automatically manages the progression through landing and trading phases
     * of the planetary encounter.
     * <p>
     * The method handles three main scenarios:
     * 1. Landing phase: Prompts players to choose planets while planets are available
     * 2. Trading phase transition: When all players have chosen, transitions to goods trading
     * 3. Encounter completion: When all trading is done, applies lost of days
     * <p>
     * Players who land on planets lose days in reverse order (last to first)
     * to maintain proper positioning on the flight path.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the planetary encounter is complete, false if phases are ongoing
     */
    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            if (landedPlayers.size() < planets.size()) {
                model.setPlayerState(players.get(playerIndex).getUsername(), PlayerState.WAIT_INDEX);
                return false;
            } else // Planets are finished
                model.setPlayerState(players.get(playerIndex).getUsername(), PlayerState.DONE);
        }

        // Check if everyone has finished
        boolean hasLanded = true;
        boolean hasLandedAndSetGoods = true;
        for (PlayerData player : players) {
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE && model.getPlayerState(player.getUsername()) != PlayerState.WAIT)
                hasLanded = false;
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasLandedAndSetGoods = false;
        }

        if (hasLanded && !hasLandedAndSetGoods) { // First phase finished, start second one
            for (PlayerData player : players)
                if (model.getPlayerState(player.getUsername()) == PlayerState.WAIT)
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT_GOODS);
        }

        if (hasLandedAndSetGoods) { // Card finished
            for (PlayerData player : players.reversed())
                if (landedPlayers.containsKey(player.getUsername()))
                    board.movePlayer(player, days * -1);
            return true;
        }
        return false;
    }

    /**
     * Processes planet selection command effects during the landing phase.
     * <p>
     * Handles player planet choices with the following logic:
     * - If value is null: player chooses not to land on any planet
     * - If value is valid planet index and planet is available: player lands on that planet
     * - If value is invalid or planet already occupied: throws exception
     * <p>
     * Planet selection follows first-come-first-served allocation, with each planet
     * accommodating only one player. Once a planet is selected, it becomes unavailable
     * for other players.
     *
     * @param commandType the type of command being executed (must be WAIT_INDEX)
     * @param value       the integer index of the chosen planet, or null to decline landing
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player making the planet choice
     * @return true if the planetary encounter progresses or completes, false otherwise
     * @throws IllegalArgumentException if the planet index is invalid or the planet is already occupied
     * @throws RuntimeException         if the command type is not WAIT_INDEX
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_INDEX) {
            if (value == null) // Players doesn't land
                model.setPlayerState(username, PlayerState.DONE);
            else if (value >= planets.size() || landedPlayers.containsValue(planets.get(value))) // Invalid index
                throw new IllegalArgumentException("Planet not valid or already occupied");
            else { // Land
                landedPlayers.put(username, planets.get(value));
                model.setPlayerState(username, PlayerState.WAIT);
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes goods trading command effects during the trading phase.
     * <p>
     * Handles the completion of goods trading for players who landed on planets.
     * The actual goods validation is performed by the doSpecificCheck method
     * which ensures trading conforms to the specific planet's reward structure.
     *
     * @param commandType the type of command being executed (must be WAIT_GOODS)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing goods trading
     * @return true if the planetary encounter progresses or completes, false otherwise
     * @throws RuntimeException if the command type is not WAIT_GOODS
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for goods trading commands based on planet rewards.
     * <p>
     * Validates that the goods changes specified by the player conform to the rewards
     * available on their chosen planet. Each planet has specific goods that can be
     * traded, and this method ensures players only trade within those constraints.
     *
     * @param commandType the command type being validated
     * @param r           the rewards parameter (unused, overridden by planet rewards)
     * @param deltaGood   the map of goods changes by color type
     * @param batteries   the list of battery components being used
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, landedPlayers.get(username).rewards(), deltaGood, batteries, username, board);
    }

    /**
     * Handles the effects when a player leaves the game during planetary encounter execution.
     * <p>
     * Manages player list updates and continues encounter progression when appropriate:
     * - Updates player indices and list to maintain proper iteration
     * - Continues encounter evaluation if the current player left
     * - Maintains planetary encounter flow for remaining players
     * <p>
     * Note: Players who leave after landing on planets will still retain their
     * planet reservation and associated benefits.
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if the planetary encounter should continue automatically, false otherwise
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

        return playerIndex == players.size() && autoCheckPlayers(model, board);
    }

}