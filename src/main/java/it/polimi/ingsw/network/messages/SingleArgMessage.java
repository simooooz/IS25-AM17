package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.User;
import it.polimi.ingsw.network.socket.server.RefToUser;

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
    public void execute(RefToUser user) {
        getMessageType().execute(this, user);
    }

    @Override
    public void execute(User user) {
        getMessageType().execute(this, user);
    }

}
