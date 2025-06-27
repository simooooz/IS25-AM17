package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;

import java.io.Serializable;

/**
 * Abstract base class for all network messages using the Command pattern.
 * Each message contains a type and can execute specific logic on client or server.
 */
public abstract class Message implements Serializable {

    /** The type of this message */
    private final MessageType messageType;

    /**
     * @param messageType the type of this message
     */
    public Message(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Executes server-side logic for this message.
     * Default implementation does nothing - should be overridden by subclasses.
     *
     * @param user the client handler
     */
    public void execute(ClientHandler user) {
        // If this code is executed it means that message is not handled by a subclass
        // What does it mean? It isn't a critical situation, server can just ignore it
    }

    /**
     * Executes client-side logic for this message.
     * Default implementation does nothing - should be overridden by subclasses.
     *
     * @param client the client socket
     */
    public void execute(ClientSocket client) {
        // If this code is executed it means that message is not handled by a subclass
        // What does it mean? It isn't a critical situation, client can just ignore it
    }

}
