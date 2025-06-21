package it.polimi.ingsw.common.dto;

public class PlayerPositionDTO {

    public PlayerDTO player;
    public int position;

    public PlayerPositionDTO(PlayerDTO player, int position) {
        this.player = player;
        this.position = position;
    }

    public PlayerPositionDTO() {}

}