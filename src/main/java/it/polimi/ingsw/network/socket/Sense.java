package it.polimi.ingsw.network.socket;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.socket.server.Server;

public class Sense {

    public static void sendSense(String connectionCode) {
        try {
            Server.getInstance().sendObject(connectionCode, new Sense());
        } catch (ServerException _) {
            // There's an error during sensing of a Sense
            // We don't need to handle this here
            // TODO sicuri? come si gestisce nel main la cosa?
        }
    }

}
