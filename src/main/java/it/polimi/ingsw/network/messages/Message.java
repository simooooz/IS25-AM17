package it.polimi.ingsw.network.messages;


import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private final MessageType messageType;
    private Object arguments;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Object getArguments() {return arguments;}

    public void execute(ClientHandler user) {
        // If this code is executed it means that message is not handled by a subclass
        // What does it mean? It isn't a critical situation, server can just ignore it
    }

    public void execute(ClientSocket client) {
        // If this code is executed it means that message is not handled by a subclass
        // What does it mean? It isn't a critical situation, client can just ignore it
    }

}
