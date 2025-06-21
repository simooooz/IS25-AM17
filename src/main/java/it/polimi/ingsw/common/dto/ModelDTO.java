package it.polimi.ingsw.common.dto;

import it.polimi.ingsw.common.model.enums.PlayerState;

import java.util.Map;

public class ModelDTO {

    public BoardDTO board;
    public Map<String, PlayerState> playersState;

    public ModelDTO() {}

}