package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap;
import java.util.List;

public class StardustCard extends Card {
    public StardustCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board) {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers().reversed();    // players by inverted position
        players.forEach(p -> {
            Ship ship = p.getKey().getShip();
            board.movePlayer(p.getKey(), -1*ship.countExposedConnectors());
        });
    }
}
