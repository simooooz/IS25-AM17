package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CriteriaType;
import it.polimi.ingsw.model.cards.utils.PenaltyCombatZone;
import it.polimi.ingsw.model.game.Board;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class CombactZoneCard extends Card{

    private final List<SimpleEntry<CriteriaType, PenaltyCombatZone>> lines;

    public CombactZoneCard(int level, boolean isLearner, List<SimpleEntry<CriteriaType, PenaltyCombatZone>> lines) {
        super(level, isLearner);
        this.lines = lines;
    }

    public List<SimpleEntry<CriteriaType, PenaltyCombatZone>> getLines() {
        return lines;
    }

    @Override
    public void resolve(Board board){}
}
