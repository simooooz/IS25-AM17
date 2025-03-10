package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CriteriaType;
import it.polimi.ingsw.model.cards.utils.PenaltyCombatZone;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class CombactZoneCard extends Card{

    private final List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines;

    public CombactZoneCard(int level, boolean isLearner, List<SimpleEntry<CriteriaType, PenaltyCombatZone>> warLines) {
        super(level, isLearner);
        this.warLines = warLines;
    }

    @Override
    public void resolve(Board board) throws Exception {
        for (SimpleEntry<CriteriaType, PenaltyCombatZone> warLine : warLines) {
            List<PlayerData> players = board.getPlayersByPos();

            PlayerData worst = players.getFirst();
            for (PlayerData player : players) // Find the worst player
                if (warLine.getKey().countCriteria(player) < warLine.getKey().countCriteria(worst))
                    worst = player;

            warLine.getValue().resolve(board, worst);
        }
    }

}