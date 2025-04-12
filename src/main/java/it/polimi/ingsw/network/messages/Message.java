package it.polimi.ingsw.network.messages;


import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private final MessageType messageType;

    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void execute(User user) {
        // If this code is executed it means that message is not handled by a sub-class
        // What does it mean? It isn't a critical situation, server can just ignore it
    };

    public void execute(UserOfClient user) {
        // If this code is executed it means that message is not handled by a sub-class
        // What does it mean? It isn't a critical situation, client can just ignore it
    };

}