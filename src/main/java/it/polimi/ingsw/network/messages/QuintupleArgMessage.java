package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;

public class QuintupleArgMessage<T1, T2, T3, T4, T5> extends Message {

    private final T1 arg1;
    private final T2 arg2;
    private final T3 arg3;
    private final T4 arg4;
    private final T5 arg5;

    public QuintupleArgMessage(MessageType messageType, T1 arg1, T2 arg2, T3 arg3, T4 arg4, T5 arg5) {
        super(messageType);
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
        this.arg4 = arg4;
        this.arg5 = arg5;
    }

    public T1 getArg1() {
        return arg1;
    }

    public T2 getArg2() {
        return arg2;
    }

    public T3 getArg3() {
        return arg3;
    }

    public T4 getArg4() {
        return arg4;
    }

    public T5 getArg5() {
        return arg5;
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
