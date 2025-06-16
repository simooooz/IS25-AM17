package it.polimi.ingsw.client.model.player;

/**
 * Represents a read-only view of a player's state on the client side.
 */
public class ClientPlayer {

    private final String username;
    private ClientShip ship;
    private int credits;
    private boolean endedInAdvance;
    private boolean leftMatch;

    public ClientPlayer(String username) {
        this.username = username;
        this.credits = 0;
        this.endedInAdvance = false;
        this.leftMatch = false;
    }

    public String getUsername() {
        return username;
    }

    public ClientShip getShip() {
        return ship;
    }

    public void setShip(ClientShip ship) {
        this.ship = ship;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setEndedInAdvance(boolean endedInAdvance) {
        this.endedInAdvance = endedInAdvance;
    }

    public boolean hasEndedInAdvance() {
        return endedInAdvance;
    }

    public void setLeftMatch(boolean leftMatch) {
        this.leftMatch = leftMatch;
    }

}