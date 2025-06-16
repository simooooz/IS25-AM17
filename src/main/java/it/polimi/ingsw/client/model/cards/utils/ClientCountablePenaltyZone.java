package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientCountablePenaltyZone extends ClientPenaltyCombatZone {

    @JsonProperty private int penaltyNumber;
    @JsonProperty private ClientMalusType penaltyType;

    public ClientCountablePenaltyZone(int penaltyNumber, ClientMalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    public ClientCountablePenaltyZone() {}

    @Override
    public String toString() {
        return penaltyNumber + " " + penaltyType.toString();
    }

}