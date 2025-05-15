package it.polimi.ingsw.controller.exceptions;

public class PlayerAlreadyInException extends RuntimeException {
    public PlayerAlreadyInException(String message) {
        super(message);
    }
}
