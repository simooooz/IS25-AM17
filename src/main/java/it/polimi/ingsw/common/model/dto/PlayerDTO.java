package it.polimi.ingsw.common.dto;

public class PlayerDTO {

    public String username;
    public ShipDTO ship;
    public int credits;
    public boolean endedInAdvance;
    public boolean leftMatch;

    public PlayerDTO(String username, ShipDTO ship, int credits, boolean endedInAdvance, boolean leftMatch) {
        this.username = username;
        this.ship = ship;
        this.credits = credits;
        this.endedInAdvance = endedInAdvance;
        this.leftMatch = leftMatch;
    }

    public PlayerDTO() {}

}