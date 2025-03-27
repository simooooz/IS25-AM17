package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

// TODO do auto checks sometimes asking user is not mandatory (i.e. if there are not enough goods)

public enum MalusType {
    DAYS {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            board.movePlayer(player, -1*penaltyNumber);
            return PlayerState.DONE;
        }
    },

    GOODS {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            return PlayerState.WAIT_REMOVE_GOODS;
        }
    },

    CREW {
        @Override
        public PlayerState resolve(int penaltyNumber, Board board, PlayerData player) {
            return PlayerState.WAIT_REMOVE_CREW;
        }
    };

    public abstract PlayerState resolve(int penaltyNumber, Board board, PlayerData player);
}
