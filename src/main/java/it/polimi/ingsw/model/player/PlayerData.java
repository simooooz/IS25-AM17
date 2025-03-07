package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.game.objects.ColorType;

public class PlayerData {

    private final ColorType color;
    private final String username;
    private final Ship ship;
    private int credits;

    public PlayerData(ColorType colorType, String username, Ship ship, int credits) {
        this.color = colorType;
        this.username = username;
        this.ship = ship;
        this.credits = credits;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return username.equals(that.username);
    }

    public ColorType getColor() {
        return color;
    }

    public String getUsername() {
        return username;
    }

    public Ship getShip() {
        return ship;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
