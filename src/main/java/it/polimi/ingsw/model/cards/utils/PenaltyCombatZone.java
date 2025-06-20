package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CannonFirePenaltyCombatZone.class, name = "CANNON_FIRE"),
        @JsonSubTypes.Type(value = CountablePenaltyZone.class, name = "COUNTABLE"),
})
public abstract class PenaltyCombatZone {

    public PenaltyCombatZone() {}

    public abstract PlayerState resolve(ModelFacade model, Board board, String username);

    public void doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public void doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    @JsonIgnore
    public int getPenaltyNumber() {
        throw new RuntimeException("Method not valid");
    }

}
