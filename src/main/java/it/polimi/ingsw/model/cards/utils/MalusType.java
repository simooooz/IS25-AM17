package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public enum MalusType {
    DAYS {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            board.movePlayer(player, -1*penaltyNumber);
            return PlayerState.DONE;
        }

        @Override
        public String toString() {
            return "📅";
        }
    },

    GOODS {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            return PlayerState.WAIT_REMOVE_GOODS;
        }

        @Override
        public String toString() {
            return  "🔲";
        }
    },

    CREW {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            return PlayerState.WAIT_REMOVE_CREW;
        }

        @Override
        public String toString() {
            return "👨🏻‍🚀";
        }
    };

    public abstract PlayerState resolve(int penaltyNumber, Board board, PlayerData player);
}
