package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;

public class ZeroArgMessage extends Message {

    public ZeroArgMessage(MessageType messageType) {
        super(messageType);
    }

    @Override
    public void execute(ClientHandler user) {
        getMessageType().execute(user, this);
    }

    @Override
    public void execute(ClientSocket client) {
        getMessageType().execute(client, this);
    }

}
