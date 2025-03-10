package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap;
import java.util.List;

public class OpenSpaceCard extends Card {
    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board) {
        List<PlayerData> players = board.getPlayersByPos();    // players by position
        players.forEach(p -> {
            Ship ship = p.getShip();
            board.movePlayer(p, ship.calcEnginePower(ship.getComponentByType(EngineComponent.class)));
        });
    }
}
