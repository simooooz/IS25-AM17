package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Card implementation representing an abandoned station encounter in the game.
 * This card presents players with an opportunity to claim valuable goods from
 * a derelict space station, but requires sufficient crew to safely dock and
 * secure the facility.
 * <p>
 * The abandoned station encounter operates on a "first come, first served" basis
 * where the first eligible player to accept the challenge claims the station and
 * its cargo. Players must have sufficient crew members to attempt the boarding
 * action, and successful claimants can acquire specific goods from the station's stores.
 * <p>
 * Unlike abandoned ships that require crew sacrifice, abandoned stations allow
 * players to claim goods without losing crew members.
 */
public class AbandonedStationCard extends Card {

    /**
     * The number of crew members required to attempt docking with the abandoned station
     */
    @JsonProperty
    private final int crew;

    /**
     * The number of flight days lost for successfully claiming the abandoned station
     */
    @JsonProperty
    private final int days;

    /**
     * The map of goods available for acquisition from the abandoned station by color type and quantity
     */
    @JsonProperty
    private final Map<ColorType, Integer> goods;

    /**
     * List of players participating in the abandoned station encounter
     */
    private List<PlayerData> players;

    /**
     * Current index of the player being evaluated for the abandoned station opportunity
     */
    private int playerIndex;

    /**
     * Flag indicating whether the abandoned station has already been claimed by a player
     */
    private boolean shipConquered;

    /**
     * Constructs a new AbandonedStationCard with the specified parameters.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     * @param crew      the number of crew members required to attempt docking
     * @param days      the number of flight days advanced for successful claiming
     * @param goods     the map of goods available for acquisition by color type and quantity
     */
    public AbandonedStationCard(int id, int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(id, level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    /**
     * Starts the abandoned station encounter by initializing player states and
     * beginning the sequential evaluation process.
     * <p>
     * Resets the encounter state, marks the station as unclaimed, and begins
     * evaluating players in order to determine who has the opportunity and
     * capability to claim the abandoned facility.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the encounter is complete, false if player decisions are required
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.shipConquered = false;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : this.players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model);
    }

    /**
     * Automatically evaluates players for eligibility and opportunity to claim the abandoned station.
     * <p>
     * The evaluation process follows these rules:
     * 1. If the station is already claimed: all remaining players are marked as done
     * 2. If a player lacks sufficient crew: they cannot attempt docking and are marked as done
     * 3. If a player has sufficient crew: they are offered the choice to attempt claiming
     * <p>
     * Players are evaluated in turn order, creating a first-come-first-served system
     * where earlier players have priority access to the abandoned station opportunity.
     * The encounter completes when all players have been evaluated or made their decisions.
     *
     * @param model the model facade providing access to game state
     * @return true if all players have been evaluated and the encounter is complete, false if waiting for player decisions
     */
    private boolean autoCheckPlayers(ModelFacade model) {
        for (; playerIndex < this.players.size(); playerIndex++) {
            PlayerData player = this.players.get(playerIndex);

            if (shipConquered)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : this.players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    /**
     * Processes docking decision command effects from eligible players.
     * <p>
     * Handles player choices about attempting to dock with the abandoned station:
     * - If player accepts the challenge: initiates goods acquisition process and marks station as claimed
     * - If player declines the challenge: marks them as done and continues to next player
     * <p>
     * The first player to accept the docking challenge claims the station, preventing
     * all subsequent players from accessing the opportunity.
     *
     * @param commandType the type of command being executed (must be WAIT_BOOLEAN)
     * @param value       the boolean value indicating whether the player accepts the docking challenge
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player making the docking decision
     * @return true if the encounter progresses automatically, false if goods acquisition is required
     * @throws RuntimeException if the command type is not WAIT_BOOLEAN
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_GOODS);
            shipConquered = true;
            return false;
        } else if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes goods acquisition completion and awards the abandoned station's movement benefits.
     * <p>
     * Handles the completion of goods acquisition after a player has successfully
     * docked with the abandoned station.
     *
     * @param commandType the type of command being executed (must be WAIT_GOODS)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing goods acquisition
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not WAIT_GOODS
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);

            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for goods acquisition commands to ensure
     * players only acquire the specific goods available in the abandoned station.
     * <p>
     * Validates that the goods changes specified by the claiming player conform to
     * the station's available inventory. This ensures players can only acquire the
     * specific types and quantities of goods that the abandoned station contains,
     * preventing exploitation of the goods acquisition system.
     *
     * @param commandType the command type being validated
     * @param r           the rewards parameter (overridden by station goods)
     * @param deltaGood   the map of goods changes by color type
     * @param batteries   the list of battery components being used
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.goods, deltaGood, batteries, username, board);
    }

    /**
     * Handles the effects when a player leaves the game during abandoned station encounter execution.
     * <p>
     * Manages player list updates and continues encounter progression when appropriate:
     * - Updates player indices and list to maintain proper iteration
     * - Continues encounter evaluation if the current player left
     * - Maintains encounter flow for remaining players
     * <p>
     * Players who leave before claiming the station do not affect the station's availability
     * for remaining players, maintaining fair access to the opportunity.
     *
     * @param state    the current state of the leaving player
     * @param model    the model facade providing access to game state
     * @param board    the game board containing remaining players and entities
     * @param username the username of the player leaving the game
     * @return true if the encounter should continue automatically, false otherwise
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
            return autoCheckPlayers(model);
        } else
            players.remove(indexOfLeftPlayer);

        return false;
    }
}