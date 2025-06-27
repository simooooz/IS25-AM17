package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

/**
 * Enumeration defining different types of penalties (malus) that can be applied
 * to players as consequences for poor performance or adverse events in the game.
 * <p>
 */
public enum MalusType {

    /**
     * Days malus type applies time-based movement penalties to players.
     */
    DAYS {
        /**
         * Applies a movement penalty to the specified player by moving them backward
         * on the flight path by the specified number of days.
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
     * goods from their cargo holds.
     */
    GOODS {
        /**
         * Initiates a goods removal penalty that requires the player to select
         * which goods to surrender from their cargo holds.
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
     * members from their ship.
     */
    CREW {
        /**
         * Initiates a crew removal penalty that requires the player to select
         * which crew members to lose from their ship's cabins.
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
     *
     * @param penaltyNumber the quantity or severity of the penalty to apply
     * @param board         the game board containing relevant game state information
     * @param username      the username of the player receiving the penalty
     * @return the PlayerState indicating how the penalty resolution should proceed
     */
    public abstract PlayerState resolve(int penaltyNumber, Board board, String username);
}