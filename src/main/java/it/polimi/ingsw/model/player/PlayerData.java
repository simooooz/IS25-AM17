package it.polimi.ingsw.model.player;

import it.polimi.ingsw.common.model.events.game.CreditsUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.FlightEndedEvent;
import it.polimi.ingsw.model.game.Board;

public class PlayerData {

    private final String username;
    private Ship ship;
    private int credits;
    private boolean endedInAdvance;
    private boolean leftGame;

    public PlayerData(String username) {
        this.username = username;
        this.credits = 0;
        this.endedInAdvance = false;
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

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
        EventContext.emit(new CreditsUpdatedEvent(username, credits));
    }

    public void endFlight() {
        endedInAdvance = true;
        EventContext.emit(new FlightEndedEvent(username));
    }

    public boolean hasEndedInAdvance() {
        return endedInAdvance;
    }

    public boolean isLeftGame() {
        return leftGame;
    }

    public void setLeftGame(boolean leftGame) {
        this.leftGame = leftGame;
    }

    public void setReady(Board board) {
        ship.getHandComponent().ifPresent(c -> c.releaseComponent(board, this));
        board.moveToBoard(this);
    }

}
