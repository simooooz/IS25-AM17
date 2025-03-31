package it.polimi.ingsw.model.exceptions;

public class NoEnoughPlayerException extends RuntimeException {
    public NoEnoughPlayerException(String message) {
        super(message);
    }
}
