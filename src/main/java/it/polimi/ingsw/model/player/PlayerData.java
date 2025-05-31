package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;

public class PlayerData {

    private final String username;
    private final Ship ship;
    private int credits;
    private boolean endedInAdvance;

    public PlayerData(String username, Ship ship) {
        this.username = username;
        this.credits = 0;
        this.endedInAdvance = false;
        this.ship = ship;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData that = (PlayerData) o;
        return username.equals(that.username);
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

    public void endFlight() {
        endedInAdvance = true;
    }

    public boolean hasEndedInAdvance() {
        return endedInAdvance;
    }

    public void setReady(Board board) {
        ship.getHandComponent().ifPresent(c -> c.releaseComponent(board, ship));
        board.moveToBoard(this);
    }

}
