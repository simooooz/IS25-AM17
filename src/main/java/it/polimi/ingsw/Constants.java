package it.polimi.ingsw;

import it.polimi.ingsw.network.messages.*;

public abstract class Constants {

    public static final int DEFAULT_SOCKET_PORT = 4030;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int HEARTBEAT_INTERVAL = 2000;
    public static final int SOCKET_TIMEOUT = 4000;

    // Socket only
    public static Message createMessage(MessageType gameEvent, Object... args) {
        Message message;
        switch (args.length) {
            case 0 -> message = new ZeroArgMessage(gameEvent);
            case 1 -> message = new SingleArgMessage<>(gameEvent, args[0]);
            case 2 -> message = new DoubleArgMessage<>(gameEvent, args[0], args[1]);
            case 3 -> message = new TripleArgMessage<>(gameEvent, args[0], args[1], args[2]);
            default -> message = new ErrorMessage("Unknown message");
        }
        return message;
    }

}
