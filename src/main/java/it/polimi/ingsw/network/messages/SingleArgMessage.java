package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.ClientSocket;

import it.polimi.ingsw.network.socket.server.ClientHandler;

public class SingleArgMessage<T> extends Message {

    private final T arg1;

    public SingleArgMessage(MessageType messageType, T arg1) {
        super(messageType);
        this.arg1 = arg1;
    }

    public T getArg1() {
        return arg1;
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
