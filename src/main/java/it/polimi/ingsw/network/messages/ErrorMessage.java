package it.polimi.ingsw.network.messages;


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

    // TODO è necessario?
    public String getMessage() {
        return message != null ? message : "Empty message";
    }

}
