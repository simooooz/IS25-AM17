package it.polimi.ingsw.model.exceptions;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException() {
        super("Player not found");
    }
}
