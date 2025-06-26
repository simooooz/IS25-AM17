package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.List;


/**
 * A read-only representation of a game card on the client side.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClientAbandonedShipCard.class, name = "ABANDONED_SHIP"),
        @JsonSubTypes.Type(value = ClientAbandonedStationCard.class, name = "ABANDONED_STATION"),
        @JsonSubTypes.Type(value = ClientCombatZoneCard.class, name = "COMBAT_ZONE"),
        @JsonSubTypes.Type(value = ClientEpidemicCard.class, name = "EPIDEMIC"),
        @JsonSubTypes.Type(value = ClientMeteorSwarmCard.class, name = "METEOR_SWARM"),
        @JsonSubTypes.Type(value = ClientOpenSpaceCard.class, name = "OPEN_SPACE"),
        @JsonSubTypes.Type(value = ClientPiratesCard.class, name = "PIRATES"),
        @JsonSubTypes.Type(value = ClientPlanetCard.class, name = "PLANET"),
        @JsonSubTypes.Type(value = ClientSlaversCard.class, name = "SLAVERS"),
        @JsonSubTypes.Type(value = ClientSmugglersCard.class, name = "SMUGGLERS"),
        @JsonSubTypes.Type(value = ClientStardustCard.class, name = "STARDUST")
})
public abstract class ClientCard {

    @JsonProperty private int id;
    @JsonProperty private int level;
    @JsonProperty protected boolean isLearner;

    public ClientCard(int id, int level, boolean isLearner) {
        this.id = id;
        this.level = level;
        this.isLearner = isLearner;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ClientCard() {}

    public List<ColorType> getReward(String username) {
        throw new RuntimeException("Method not valid");
    }

    public String printCardInfo(ClientGameModel model, ClientBoard board) {
        return "";
    }

} 