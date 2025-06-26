package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

/**
 * Card implementation representing a slaver encounter in the game.
 * This card extends the basic enemy encounter mechanics with slaver-specific
 * penalties and rewards, focusing on crew-related consequences.
 * <p>
 * The slaver encounter follows the standard enemy combat pattern where players
 * attempt to defeat the slavers using cannon firepower. Players who successfully
 * defeat the slavers are offered rewards (credits and movement benefits), while
 * players who fail face the specific penalty of losing crew members to the slavers.
 * <p>
 * The slavers specifically target crew members, representing their primary
 * interest in capturing living beings for their slave trade operations.
 * Defeated players must surrender a specified number of crew members from
 * their cabin components.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class SlaversCard extends EnemiesCard {

    /**
     * The number of crew members that defeated players must surrender to the slavers
     */
    @JsonProperty
    private final int crew;

    /**
     * The number of credits offered as reward for defeating the slavers
     */
    @JsonProperty
    private final int credits;

    /**
     * Constructs a new SlaversCard with the specified parameters.
     *
     * @param id               the unique identifier of the card
     * @param level            the level of the card
     * @param isLearner        whether this card is for learner mode
     * @param crew             the number of crew members that defeated players must surrender
     * @param credits          the number of credits offered as reward for victory
     * @param days             the number of flight days this encounter represents
     * @param slaversFirePower the firepower strength of the slaver forces
     */
    public SlaversCard(int id, int level, boolean isLearner, int crew, int credits, int days, int slaversFirePower) {
        super(id, level, isLearner, days, slaversFirePower);
        this.crew = crew;
        this.credits = credits;
    }

    /**
     * Applies the specific penalty for players defeated by the slavers by initiating
     * crew removal proceedings.
     * <p>
     * When a player fails to defeat the slavers, they must surrender crew members
     * to represent the slavers' primary objective of capturing people for their
     * slave trade operations. This penalty directly targets the player's crew
     * resources rather than causing ship damage or other penalties.
     *
     * @param model  the model facade providing access to game state
     * @param player the player data for the defeated player
     * @return true as crew removal requires player interaction to select which crew to surrender
     */
    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_CREW);
        return true;
    }

    /**
     * Processes reward choice command effects for players who successfully defeated the slavers.
     * <p>
     * Handles the player's decision on whether to claim the victory rewards:
     * - If rewards accepted: player receives credits and movement benefits
     * - If rewards declined: player receives no benefits but avoids potential risks
     * <p>
     * The movement benefit represents advancing further along the flight path
     * due to successful navigation through slaver-controlled space.
     *
     * @param commandType the type of command being executed (must be WAIT_BOOLEAN)
     * @param value       the boolean value indicating whether the player accepts the rewards
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player making the reward choice
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not WAIT_BOOLEAN
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Processes crew removal command effects for players who were defeated by the slavers.
     * <p>
     * Handles the completion of crew surrender after a player has selected which
     * crew members to give up to the slavers. The crew removal validation is
     * performed by the doSpecificCheck method to ensure the correct number of
     * crew members are surrendered.
     *
     * @param commandType the type of command being executed (must be WAIT_REMOVE_CREW)
     * @param model       the model facade providing access to game state
     * @param board       the game board containing all players and entities
     * @param username    the username of the player completing crew removal
     * @return true if the encounter progresses automatically, false if waiting for more actions
     * @throws RuntimeException if the command type is not WAIT_REMOVE_CREW
     */
    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    /**
     * Performs specific validation checks for crew removal commands to ensure
     * the correct number of crew members are surrendered to the slavers.
     * <p>
     * Validates that the specified cabin components and crew removal count match
     * the slaver's demands. The validation uses the crew field (number of crew
     * members required) rather than the provided toRemove parameter to ensure
     * consistency with the slaver encounter's specific requirements.
     *
     * @param commandType the command type being validated (must be WAIT_REMOVE_CREW)
     * @param cabins      the list of cabin components from which crew will be removed
     * @param toRemove    the number parameter (overridden by the crew requirement)
     * @param username    the username of the player executing the command
     * @param board       the game board containing all players and entities
     */
    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

}