package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientWarLine {

    @JsonProperty private ClientCriteriaType criteriaType;
    @JsonProperty private ClientPenaltyCombatZone penalty;

    public ClientWarLine() {}

    public ClientCriteriaType getCriteriaType() {
        return criteriaType;
    }

    public ClientPenaltyCombatZone getPenalty() {
        return penalty;
    }

}
