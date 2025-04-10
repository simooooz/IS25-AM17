package it.polimi.ingsw.network.exceptions;

public class UserNotFoundException extends Exception {

    public UserNotFoundException() { super(); }

    public UserNotFoundException(String message) {
        super(message);
    }

}
