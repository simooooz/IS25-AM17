package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Map;

/**
 * Card implementation representing a smuggler encounter in the game.
 * This card extends the basic enemy encounter mechanics with smuggler-specific
 * penalties and rewards, focusing on goods trading and confiscation.
 * <p>
 * The smuggler encounter follows the standard enemy combat pattern where players
 * attempt to defeat the smugglers using cannon firepower. Players who successfully
 * defeat the smugglers are offered rewards in the form of specific goods,
 * while players who fail face the penalty of having goods
 * confiscated by the smugglers.
 */
public class SmugglersCard extends EnemiesCard {

    /**
     * The number of goods that defeated players must surrender to the smugglers
     */
    @JsonProperty
    private final int penalty;

    /**
     * The map of goods by color type offered as rewards for defeating the smugglers
     */
    @JsonProperty
    private final Map<ColorType, Integer> reward;

    /**
     * Constructs a new SmugglersCard with the specified parameters.
     *
     * @param id                 the unique identifier of the card
     * @param level              the level of the card
     * @param isLearner          whether this card is for learner mode
     * @param smugglersFirePower the firepower strength of the smuggler forces
     * @param penalty            the number of goods that defeated players must surrender
     * @param reward             the map of goods by color type offered as victory rewards
     * @param days               the number of flight days this encounter represents
     */
    public SmugglersCard(int id, int level, boolean isLearner, int smugglersFirePower, int penalty, Map<ColorType, Integer> reward, int days) {
        super(id, level, isLearner, days, smugglersFirePower);
        this.penalty = penalty;
        this.reward = reward;
    }

    /**
     * Applies the specific penalty for players defeated by the smugglers by initiating
     * goods confiscation proceedings.
     * <p>
     * @param model  the model facade providing access to game state
     * @param player the player data for the defeated player
     * @return true as goods removal requires player interaction to select which goods to surrender
     */
    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_GOODS);
        return true;
    }

    /**
     * Processes reward choice command effects for players who successfully defeated the smugglers.
     * <p>
     * Handles the player's decision on whether to claim the victory rewards:
     * - If rewards accepted: player loses flight days and proceeds to goods acquisition
     * - If rewards declined: player receives no benefits and encounter ends for them
     * <p>
     * @param commandType the type of command being executed (must be WAIT_BOOLEAN)
     * @param value       the boolean value indicating whether the player accepts the rewards
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player making the reward choice
     * @return true if the encounter progresses automatically, false if player needs to handle goods
     * @throws RuntimeException if the command type is not WAIT_BOOLEAN
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * this.days);
                model.setPlayerState(username, PlayerState.WAIT_GOODS);
                return false;
            } else {
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    /**
     * Processes goods-related command effects for both reward acquisition and penalty enforcement.
     * <p>
     * Handles the completion of goods transactions for players in two scenarios:
     * - WAIT_GOODS: Player completing acquisition of reward goods after accepting victory rewards
     * - WAIT_REMOVE_GOODS: Player completing surrender of goods after being defeated
     *
     * @param commandType the type of command being executed (WAIT_GOODS or WAIT_REMOVE_GOODS)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing goods transactions
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not goods-related
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for goods removal commands to ensure
     * the correct number of goods are surrendered to the smugglers.
     * <p>
     * Validates that the goods changes specified by defeated players conform to
     * the penalty requirements. The validation uses the penalty field (number of
     * goods required) rather than the provided number parameter to ensure
     * consistency with the smuggler encounter's specific demands.
     *
     * @param commandType the command type being validated
     * @param number      the number parameter (overridden by penalty requirement)
     * @param deltaGood   the map of goods changes by color type
     * @param batteries   the list of battery components being used
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, penalty, deltaGood, batteries, username, board);
    }

    /**
     * Performs specific validation checks for goods acquisition commands to ensure
     * players only acquire the specific goods offered as smuggler rewards.
     * <p>
     * Validates that the goods changes specified by victorious players conform to
     * the reward structure offered by the smugglers. This ensures players can only
     * acquire the specific types and quantities of goods that the smugglers have
     * available for trade.
     *
     * @param commandType the command type being validated
     * @param r           the rewards parameter (overridden by smuggler rewards)
     * @param deltaGood   the map of goods changes by color type
     * @param batteries   the list of battery components being used
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.reward, deltaGood, batteries, username, board);
    }

}