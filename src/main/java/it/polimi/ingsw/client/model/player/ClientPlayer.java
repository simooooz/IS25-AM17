package it.polimi.ingsw.client.model.player;

import it.polimi.ingsw.common.dto.PlayerDTO;
import it.polimi.ingsw.view.TUI.Chroma;

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

    public ClientPlayer(PlayerDTO dto, ClientShip ship) {
        this.username = dto.username;
        this.credits = dto.credits;
        this.endedInAdvance = dto.endedInAdvance;
        this.leftMatch = dto.leftMatch;
        this.ship = ship;
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

    public boolean isLeftMatch() {
        return leftMatch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username);
        if (leftMatch)
            sb.append(Chroma.color(" (left game)", Chroma.GREY_BOLD));
        else if (endedInAdvance)
            sb.append(Chroma.color(" (ended flight)", Chroma.GREY_BOLD));
        return sb.toString();
    }

}