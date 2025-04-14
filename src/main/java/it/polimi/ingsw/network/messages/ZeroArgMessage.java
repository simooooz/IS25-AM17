package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

public class ZeroArgMessage extends Message {

    public ZeroArgMessage(MessageType messageType) {
        super(messageType);
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
