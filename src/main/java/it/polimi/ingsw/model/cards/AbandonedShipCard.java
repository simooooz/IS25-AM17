package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

/**
 * Card implementation representing an abandoned ship encounter in the game.
 * This card presents players with an opportunity to claim valuable resources
 * from a derelict vessel, but requires sufficient crew to safely board and
 * secure the abandoned ship.
 * <p>
 * The abandoned ship encounter operates on a "first come, first served" basis
 * where the first eligible player to accept the challenge claims the ship and
 * its rewards. Players must have sufficient crew members to attempt the boarding
 * action, and successful claimants must sacrifice crew members to secure the vessel.
 * <p>
 * Once a player successfully claims the abandoned ship, all subsequent players
 * are excluded from the opportunity, representing the fact that the ship has
 * already been secured. This creates strategic timing decisions about when
 * to attempt claiming valuable but risky opportunities.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class AbandonedShipCard extends Card {

    /**
     * The number of crew members required to attempt boarding the abandoned ship
     */
    @JsonProperty
    private final int crew;

    /**
     * The number of credits awarded for successfully claiming the abandoned ship
     */
    @JsonProperty
    private final int credits;

    /**
     * The number of flight days advanced for successfully claiming the abandoned ship
     */
    @JsonProperty
    private final int days;

    /**
     * Current index of the player being evaluated for the abandoned ship opportunity
     */
    private int playerIndex;

    /**
     * List of players participating in the abandoned ship encounter
     */
    private List<PlayerData> players;

    /**
     * Flag indicating whether the abandoned ship has already been claimed by a player
     */
    private boolean shipConquered;

    /**
     * Constructs a new AbandonedShipCard with the specified parameters.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     * @param crew      the number of crew members required to attempt boarding
     * @param credits   the number of credits awarded for successful claiming
     * @param days      the number of flight days advanced for successful claiming
     */
    public AbandonedShipCard(int id, int level, boolean isLearner, int crew, int credits, int days) {
        super(id, level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    /**
     * Starts the abandoned ship encounter by initializing player states and
     * beginning the sequential evaluation process.
     * <p>
     * Resets the encounter state, marks the ship as unclaimed, and begins
     * evaluating players in order to determine who has the opportunity and
     * capability to claim the abandoned vessel.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and entities
     * @return true if the encounter is complete, false if player decisions are required
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model);
    }

    /**
     * Automatically evaluates players for eligibility and opportunity to claim the abandoned ship.
     * <p>
     * The evaluation process follows these rules:
     * 1. If the ship is already claimed: all remaining players are marked as done
     * 2. If a player lacks sufficient crew: they cannot attempt boarding and are marked as done
     * 3. If a player has sufficient crew: they are offered the choice to attempt boarding
     * <p>
     * Players are evaluated in turn order, creating a first-come-first-served system
     * where earlier players have priority access to the abandoned ship opportunity.
     * The encounter completes when all players have been evaluated or made their decisions.
     *
     * @param model the model facade providing access to game state
     * @return true if all players have been evaluated and the encounter is complete, false if waiting for player decisions
     */
    private boolean autoCheckPlayers(ModelFacade model) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

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
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    /**
     * Processes boarding decision command effects from eligible players.
     * <p>
     * Handles player choices about attempting to board the abandoned ship:
     * - If player accepts the challenge: initiates crew sacrifice process and marks ship as claimed
     * - If player declines the challenge: marks them as done and continues to next player
     * <p>
     * The first player to accept the boarding challenge claims the ship, preventing
     * all subsequent players from accessing the opportunity. This creates strategic
     * risk/reward decisions about timing and resource allocation.
     *
     * @param commandType the type of command being executed (must be WAIT_BOOLEAN)
     * @param value       the boolean value indicating whether the player accepts the boarding challenge
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player making the boarding decision
     * @return true if the encounter progresses automatically, false if crew sacrifice is required
     * @throws RuntimeException if the command type is not WAIT_BOOLEAN
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);
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
     * Processes crew sacrifice completion and awards the abandoned ship's rewards.
     * <p>
     * Handles the completion of crew sacrifice after a player has successfully
     * boarded the abandoned ship. Awards the player with credits and movement
     * benefits as compensation for claiming the derelict vessel and sacrificing
     * crew members to secure it.
     * <p>
     * The movement benefit represents advancing further along the flight path
     * due to resources or intelligence gained from the abandoned ship.
     *
     * @param commandType the type of command being executed (must be WAIT_REMOVE_CREW)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing crew sacrifice
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not WAIT_REMOVE_CREW
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);
            player.setCredits(credits + player.getCredits());

            playerIndex++;
            return autoCheckPlayers(model);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for crew sacrifice commands to ensure
     * the correct number of crew members are committed to the boarding action.
     * <p>
     * Validates that the specified cabin components and crew removal count match
     * the abandoned ship's boarding requirements. The validation uses the crew
     * field (number of crew members required) rather than the provided toRemove
     * parameter to ensure consistency with the encounter's specific demands.
     *
     * @param commandType the command type being validated (must be WAIT_REMOVE_CREW)
     * @param cabins      the list of cabin components from which crew will be removed
     * @param toRemove    the number parameter (overridden by crew requirement)
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

    /**
     * Handles the effects when a player leaves the game during abandoned ship encounter execution.
     * <p>
     * Manages player list updates and continues encounter progression when appropriate:
     * - Updates player indices and list to maintain proper iteration
     * - Continues encounter evaluation if the current player left
     * - Maintains encounter flow for remaining players
     * <p>
     * Players who leave before claiming the ship do not affect the ship's availability
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