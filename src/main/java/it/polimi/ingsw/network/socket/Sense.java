package it.polimi.ingsw.network.socket;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.socket.server.Server;

import java.io.Serializable;

public class Sense implements Serializable {

    public static void sendSense(String connectionCode) {
        try {
            Server.getInstance().sendObject(connectionCode, new Sense());
        } catch (ServerException _) {
            // There's an error during sensing of a Sense
            // We don't need to handle this here
        }
    }

}
