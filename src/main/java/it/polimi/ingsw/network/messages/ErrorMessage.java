package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.server.User;

public class ErrorMessage extends Message {

    private final String message;

    public ErrorMessage(String message) {
        super(MessageType.ERROR);
        this.message = message;
    }

    public ErrorMessage() {
        super(MessageType.ERROR);
        this.message = null;
    }

    public String getMessage() {
        return message != null ? message : "Empty message";
    }

    @Override
    public void execute(User user) {
        // TODO non ha un metodo execute perché è server -> client
    }

}
