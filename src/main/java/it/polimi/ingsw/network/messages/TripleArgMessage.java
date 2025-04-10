package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

public class TripleArgMessage<T1, T2, T3> extends Message {

    private final T1 arg1;
    private final T2 arg2;
    private final T3 arg3;

    public TripleArgMessage(MessageType messageType, T1 arg1, T2 arg2, T3 arg3) {
        super(messageType);
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
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

    @Override
    public void execute(User user) {
        getMessageType().execute(this, user);
    }

    @Override
    public void execute(UserOfClient user) {
        getMessageType().execute(this, user);
    }

}
