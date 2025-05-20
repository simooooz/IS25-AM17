package it.polimi.ingsw.network.messages;


import it.polimi.ingsw.network.socket.client.ClientSocket;

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
    public void execute(ClientSocket client) {
        client.getViewTui().displayError(message);
    }

}
