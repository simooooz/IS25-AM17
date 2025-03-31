package it.polimi.ingsw.model.exceptions;

public class PlayerAlreadyInException extends RuntimeException {
    public PlayerAlreadyInException(String message) {
        super(message);
    }
}
