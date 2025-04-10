package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

public class DoubleArgMessage<T1, T2> extends Message {

    private final T1 arg1;
    private final T2 arg2;

    public DoubleArgMessage(MessageType messageType, T1 arg1, T2 arg2) {
        super(messageType);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public T1 getArg1() {
        return arg1;
    }

    public T2 getArg2() {
        return arg2;
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
