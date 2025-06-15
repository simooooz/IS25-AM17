package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public class CountablePenaltyZone extends PenaltyCombatZone {

    @JsonProperty private final int penaltyNumber;
    @JsonProperty private final MalusType penaltyType;

    public CountablePenaltyZone(int penaltyNumber, MalusType penaltyType) {
        this.penaltyNumber = penaltyNumber;
        this.penaltyType = penaltyType;
    }

    @Override
    public PlayerState resolve(ModelFacade model, Board board, PlayerData player) {
        return penaltyType.resolve(penaltyNumber, board, player);
    }

    @Override
    @JsonProperty
    public int getPenaltyNumber() {
        return penaltyNumber;
    }

    @JsonProperty
    public MalusType getPenaltyType() {
        return penaltyType;
    }

}