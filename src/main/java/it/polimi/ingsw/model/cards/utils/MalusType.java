package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

/**
 * Enumeration defining different types of penalties (malus) that can be applied
 * to players as consequences for poor performance or adverse events in the game.
 * <p>
 * Each malus type represents a different category of punishment that affects
 * specific aspects of the player's game state. The penalties range from immediate
 * effects that execute automatically to interactive penalties that require player
 * decisions about which resources to sacrifice.
 * <p>
 * The malus system provides flexibility in penalty design, allowing different
 * encounter types to apply appropriate consequences that match their thematic
 * context (e.g., time delays for navigation errors, resource loss for combat defeats).
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public enum MalusType {

    /**
     * Days malus type applies time-based movement penalties to players.
     * This represents delays in journey progress due to setbacks, detours,
     * or other circumstances that slow down the player's advancement along
     * the flight path.
     * <p>
     * Days penalties execute immediately without requiring player interaction,
     * as they represent unavoidable consequences of the triggering event.
     * The penalty amount determines how many positions backward the player moves.
     */
    DAYS {
        /**
         * Applies a movement penalty to the specified player by moving them backward
         * on the flight path by the specified number of days.
         * <p>
         * This penalty type executes immediately as it represents time-based setbacks
         * that cannot be avoided or mitigated by player choices. The movement is
         * calculated as a negative value to represent backward progress along the journey.
         *
         * @param penaltyNumber the number of days (positions) to move the player backward
         * @param board the game board containing player position information
         * @param username the username of the player receiving the penalty
         * @return PlayerState.DONE as the penalty executes immediately without further interaction
         */
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, String username) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, -1 * penaltyNumber);
            return PlayerState.DONE;
        }
    },

    /**
     * Goods malus type applies penalties that require players to surrender
     * goods from their cargo holds. This represents confiscation, theft,
     * or consumption of valuable cargo as a consequence of adverse events.
     * <p>
     * Goods penalties require player interaction to determine which specific
     * goods to surrender, allowing players some control over which resources
     * they lose while still enforcing the penalty amount.
     */
    GOODS {
        /**
         * Initiates a goods removal penalty that requires the player to select
         * which goods to surrender from their cargo holds.
         * <p>
         * This penalty type requires player interaction as the player must choose
         * which specific goods to give up. The penalty amount is enforced through
         * validation during the goods removal process, but the player retains
         * tactical control over which types of goods to sacrifice.
         *
         * @param penaltyNumber the number of goods the player must surrender
         * @param board the game board containing player and ship information
         * @param username the username of the player receiving the penalty
         * @return PlayerState.WAIT_REMOVE_GOODS to prompt the player for goods selection
         */
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, String username) {
            return PlayerState.WAIT_REMOVE_GOODS;
        }
    },

    /**
     * Crew malus type applies penalties that require players to lose crew
     * members from their ship. This represents casualties, desertions,
     * or forced recruitment by hostile forces.
     * <p>
     * Crew penalties require player interaction to determine which specific
     * crew members to lose, allowing players to make strategic decisions
     * about which cabins to affect while still enforcing the penalty amount.
     */
    CREW {
        /**
         * Initiates a crew removal penalty that requires the player to select
         * which crew members to lose from their ship's cabins.
         * <p>
         * This penalty type requires player interaction as the player must choose
         * which cabins to affect and which crew members (human or alien) to lose.
         * The penalty amount is enforced through validation during the crew removal
         * process, but the player retains tactical control over the distribution
         * of losses across their ship.
         *
         * @param penaltyNumber the number of crew members the player must lose
         * @param board the game board containing player and ship information
         * @param username the username of the player receiving the penalty
         * @return PlayerState.WAIT_REMOVE_CREW to prompt the player for crew selection
         */
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, String username) {
            return PlayerState.WAIT_REMOVE_CREW;
        }
    };

    /**
     * Abstract method that must be implemented by each malus type to define
     * how that specific penalty should be applied to the affected player.
     * <p>
     * Each implementation should:
     * 1. Either execute the penalty immediately (for automatic penalties)
     * 2. Or initiate the penalty process by setting the appropriate player state
     * 3. Handle any immediate effects that don't require player interaction
     * 4. Return the appropriate player state for continued penalty resolution
     *
     * @param penaltyNumber the quantity or severity of the penalty to apply
     * @param board         the game board containing relevant game state information
     * @param username      the username of the player receiving the penalty
     * @return the PlayerState indicating how the penalty resolution should proceed
     */
    public abstract PlayerState resolve(int penaltyNumber, Board board, String username);
}