package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;


public class OpenSpaceCard extends Card {
    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board) {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers()
                .stream()
                .sorted(Comparator.comparing((AbstractMap.SimpleEntry<PlayerData, Integer> entry) -> entry.getValue()).reversed())
                .toList();
        players.forEach(p -> {
            Ship ship = p.getKey().getShip();
            int engPower;
            if ( ( engPower = ship.calcEnginePower(ship.getComponentByType(EngineComponent.class)) ) != 0 ) {
                board.movePlayer(p.getKey(), engPower);
            } else {
                board.moveToStartingDeck(p.getKey());
            }
        });
    }
}
