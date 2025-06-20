package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WarLine {

    @JsonProperty private CriteriaType criteriaType;
    @JsonProperty private PenaltyCombatZone penalty;

    public WarLine(CriteriaType criteriaType, PenaltyCombatZone penalty) {
        this.criteriaType = criteriaType;
        this.penalty = penalty;
    }

    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    public PenaltyCombatZone getPenalty() {
        return penalty;
    }

}
