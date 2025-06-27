package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientCannonFirePenaltyCombatZone.class, name = "CANNON_FIRE"),
        @JsonSubTypes.Type(value = ClientCountablePenaltyZone.class, name = "COUNTABLE"),
})
public abstract class ClientPenaltyCombatZone {

    public ClientPenaltyCombatZone() {}

    public String printCardInfo() {
        return "";
    }

}
